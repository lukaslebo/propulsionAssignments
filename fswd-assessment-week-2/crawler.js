const loki = require("lokijs");
const lfsa = require('lokijs/src/loki-fs-structured-adapter.js');
const request = require('request');
const cheerio = require("cheerio");
const { URL } = require('url');

// Load / create database
var __db__ = new loki('webCrawler.db', {
    adapter : new lfsa(),
    autoload: true,
    autoloadCallback: databaseInitialize, // will be called once DB is loaded
    autosave: true, 
    autosaveInterval: 4000 // save every 4000ms (4s)
});

// create a new instance of the Crawler
const myCrawler = new Crawler(); 

// Will be called after DB is loaded
function databaseInitialize() {
    console.log('Database loaded.');
    // start up crawler
    myCrawler.start_up();
}

// constructor
function Crawler(maxPagesToCrawl = 10000, firstPage = "") {
    this.link = firstPage;
    this.link_protocol = "";
    this.link_domain = "";
    this.link_href = "";
    this.link_origin = "";
    this.link_relativePath = "";
    this.linkList = [];
    this.internal_list = [];
    this.external_list = [];
    this.entry = {};
    this.pagesCrawled = 0;
    this.maxPagesToCrawl = maxPagesToCrawl;
    this.errorCount = 0;
    this._websites = {};
}

Crawler.prototype.start_up = function() {
    // Assign collection
    this._websites = __db__.getCollection('websites');
    if (!this._websites) {
        this._websites = __db__.addCollection('websites');
    }
    // Start crawler depending on state of DB and if Starting page is provided
    let testForEntry = this._websites.findOne({isCrawled: false});
    if (this.link.length > 0) {
        // if starting page is provided crawl it
        console.log('Starting page provided.');
        this.crawl();
    }
    else if (!testForEntry) {
        let propulsion = 'https://www.propulsionacademy.com';
        testForEntry = this._websites.findOne({origin: propulsion});
        // if DB is empty start with propulsion
        if (!testForEntry) {
            console.log('DB is empty - starting with '+propulsion);
            this.link = propulsion;
            this.crawl();
        }
        else {
            console.log('Every page in DB has been crawled.');
        }
    }
    else {
        // crawl any page that has not yet been crawled
        console.log('Crawling next uncrawled page.');
        this.crawl_next();
    }
}

Crawler.prototype.crawl = function(pageToCrawl = this.link) {
    this.link = pageToCrawl;
    this.parse_url();

    // request website
    that = this;
    console.log("Requesting "+this.link);
    request(this.link, function (error, response, body) {
        if (error) {
            console.log('Error recieved:');
            console.log(error);
            that.errorCount++;
            if (that.errorCount < 3) {
                // try again
                that.crawl_next();
            }
            else {
                // page does not respond - set that page to crawled
                that.errorCount = 0;
                console.log('Page will not load - crawl next page');
                that.update_isCrawled();
                that.crawl_next();
            }
        }
        else if (body.match(/^<!DOCTYPE html/i)) {
            that.errorCount = 0;
            console.log('Received HTML data - processing ...');
            that.process_html(body);
            that.filter_links();
            that.store_unique();
            that.update_isCrawled();
            that.pagesCrawled++;
            if (that.pagesCrawled < that.maxPagesToCrawl) {
                that.crawl_next();
            }
            else {
                that.finished();
            }
        }
        else {
            that.errorCount = 0;
            // if website has no valid html - set that page to crawled
            console.log('invalid HTML - crawl next page');
            that.update_isCrawled();
            that.crawl_next();
        }
    });
}

// Create new obj for DB entry
Crawler.prototype.createNew = function(protocol, domain, origin, isCrawled = false) {
    let obj = {
        protocol: protocol,
        domain: domain,
        origin: origin,
        relativePaths: [],
        pathIsCrawled: [],
        isCrawled: isCrawled
    };
    return obj;
}

Crawler.prototype.parse_url = function() {
    const parsed = new URL(this.link);
    this.link_domain = parsed.host;
    this.link_protocol = parsed.protocol;
    this.link_relativePath = parsed.pathname;
    this.link_href = parsed.href;
    this.link_origin = parsed.origin;
}

Crawler.prototype.process_html = function(body) {
    const $ = cheerio.load(body)
    const anchors = $('a');
    this.linkList = [];
    for (let i = 0; i < anchors.length; i++) {
        let href = anchors[i].attribs.href;
        if (href != undefined && href != null && !href.match(/^#/) && !href.match(/^mailto/i)) {
            let check = href.replace(/[^.]/g,'').length;
            if (href.match(/^\//)) {
                // relative link - prepend origin
                this.linkList.push(this.link_origin+href); 
            }
            else if (check == 0) {
                this.linkList.push(this.link_origin+'/'+href);
            }
            else {
                this.linkList.push(href);
            }
        }
    }
}

Crawler.prototype.filter_links = function() {
    for (let i = 0; i < this.linkList.length; i++) {
        let parsed = new URL(this.linkList[i]);
        let obj = {
            protocol: parsed.protocol,
            domain: parsed.host,
            origin: parsed.origin,
            path: parsed.pathname,
            href: parsed.href
        };
        if (this.link_domain == obj.domain) {
            this.internal_list.push(obj);
        }
        else {
            this.external_list.push(obj);
        }
    }
}

Crawler.prototype.store_unique = function() {
    let index = -1;
    this.entry = this._websites.findOne({domain: this.link_domain});
    if (!this.entry) {
        // when started with empty DB
        console.log('DB is empty. Storing ' + this.link_origin);
        let el = this.createNew(this.link_protocol, this.link_domain, this.link_origin);
        if (this.link_relativePath.length > 0) {
            console.log('Adding relative path '+ this.link_relativePath);
            el.relativePaths.push(this.link_relativePath);
            el.pathIsCrawled.push(true);
        }
        this._websites.insert(el);
        this.entry = this._websites.findOne({domain: this.link_domain});
    }

    // insert new relative paths
    console.log('Adding relative paths for ' + this.link_origin);
    for (let i = 0; i < this.internal_list.length; i++) {
        index = this.entry.relativePaths.indexOf(this.internal_list[i].path);
        if (index == -1 && this.internal_list[i].path.length > 0) {
            console.log('\t'+this.internal_list[i].path);
            this.entry.relativePaths.push(this.internal_list[i].path);
            this.entry.pathIsCrawled.push(false);
        }
    }
    this._websites.update(this.entry);

    // insert new external links
    for (let i = 0; i < this.external_list.length; i++) {
        this.entry = this._websites.findOne({domain: this.external_list[i].domain});
        if (!this.entry) {
            // insert new
            console.log('New domain found: ' + this.external_list[i].origin);
            let newPage = this.createNew(this.external_list[i].protocol, this.external_list[i].domain, this.external_list[i].origin);
            if (this.external_list[i].path.length > 0) {
                newPage.relativePaths.push(this.external_list[i].path);
                newPage.pathIsCrawled.push(false);
            }
            this._websites.insert(newPage);
        }
        else {
            // not new but check if this relative path is new
            index = this.entry.relativePaths.indexOf(this.external_list[i].path);
            if (index == -1 && this.external_list[i].path.length > 0) {
                console.log('New relative path for ' + this.external_list[i].origin + ' -- ' + this.external_list[i].origin + this.external_list[i].path);
                this.entry.relativePaths.push(this.external_list[i].path);
                this.entry.pathIsCrawled.push(false);
            }
            this._websites.update(this.entry);
        }
    }
}

Crawler.prototype.update_isCrawled = function() {
    this.entry = this._websites.findOne({domain: this.link_domain});
    if (this.entry) {
        let index = this.entry.relativePaths.indexOf(this.link_relativePath);
        if (index > -1) {
            this.entry.pathIsCrawled[index] = true;
        }
        index = this.entry.pathIsCrawled.indexOf(false);
        if (index == -1) {
            this.entry.isCrawled = true;
        }
        this._websites.update(this.entry);
    }
}

Crawler.prototype.crawl_next = function() {
    let index = -1;
    let nextPage = "";
    this.entry = this._websites.findOne({domain: this.link_domain});
    if (!this.entry || this.entry.isCrawled) {
        // crawl next site
        this.entry = this._websites.findOne({isCrawled: false});
        if (!this.entry) {
            console.log('All pages in DB have been crawled.'); // <= this is the goal ;-)
            return;
        }
        nextPage = this.entry.origin;
        index = this.entry.pathIsCrawled.indexOf(false);
        if (index > -1) {
            nextPage += this.entry.relativePaths[index];
        }
        this.link = nextPage;
        this.crawl();
    }
    else {
        // crawl next relative path
        index = this.entry.pathIsCrawled.indexOf(false);
        nextPage = this.entry.origin + this.entry.relativePaths[index];
        this.link = nextPage;
        this.crawl();
    }
}

Crawler.prototype.finished = function() {
    let collectionSize = this._websites.data.length;
    let totalPathSize = 0;
    for (let i = 0; i < collectionSize; i++) {
        totalPathSize += this._websites.data[i].relativePaths.length;
    }
    // done...
    console.log(`Crawled ${this.pagesCrawled} pages.`);
    console.log(`Number of known domains: ${collectionSize}`);
    console.log(`Number of known URL's: ${totalPathSize}`);
}
