const loki = require("lokijs");
const lfsa = require('lokijs/src/loki-fs-structured-adapter.js');
var request = require('request');
const cheerio = require("cheerio");
const { URL } = require('url');

var db = new loki('webCrawler.db', {
    adapter : new lfsa(),
    autoload: true,
    autoloadCallback : databaseInitialize, // will be called once DB is loaded
    autosave: true, 
    autosaveInterval: 4000 // save every 4000ms (4s)
});
const _websites = db.addCollection('websites');

function databaseInitialize() {
    let propWeb = 'https://www.propulsionacademy.com';
    let myurl = new URL(propWeb);
    const myCrawler = new CrawlerApp();
    // let el = myCrawler.DBelement(myurl.protocol, myurl.host, myurl.href);
    // _websites.insert(el);
    myCrawler.crawler(propWeb);
}

function CrawlerApp() {
    this.currentLink = "";
    this.domain = "";
}

CrawlerApp.prototype.crawler = function(link) {
    this.currentLink = link;
    let thisURL = new URL(link);
    this.domain = thisURL.host;
    console.log('Requesting: '+link);
    const that = this;
    request(link, function (error, response, body) {
        if (error) {
            console.log('Error recieved:');
            console.log(error);
        }
        if (!error && body.match(/^<!DOCTYPE html/i)) {
            // crawl the path
            console.log('Data received. Processing data...');
            let linkList = that.process_request(body);
            let filteredList = that.filter_links(linkList);
            // store all the links found into DB
            console.log('Storing unique links.');
            that.store_unique( filteredList);
            // set isCrawled of this site / path to true
            that.set_booleans();

            // if this website has a relative path which has not been crawled, crawl that path
            let thisEntry = _websites.findOne({domain: this.domain});
            let index = 0;
            let check = false;
            let newLink = "";
            while (index < thisEntry.relativePaths.length) {
                if (thisEntry.pathIsCrawled[index] == false) {
                    newLink = thisEntry.href;
                    if (newLink[newLink.length-1]== "/") {
                        newLink = newLink.substring(0,newLink.length-1);
                    }
                    newLink += newLink + thisEntry.relativePaths[index];
                    console.log('Crawling relative path: '+newLink);
                    check = true;
                    break;
                }
                ++index;
            }
            if (check) {
                that.crawler(newLink);
            }
            
            // find another website that has not yet been crawled
            thisEntry = _websites.findOne({isCrawled: false});
            newLink = thisEntry.href;
            if (newLink[newLink.length-1]== "/") {
                newLink = newLink.substring(0,newLink.length-1);
            }
            if (thisEntry.relativePaths.length > 0) {
                newLink += thisEntry.relativePaths[0];
            }
            if (newLink.length > 0) {
                console.log('Crwaling new website: '+ newLink);
                that.crawler(newLink);
            }

        }
        else {
            console.log('Link dismissed...')
        }
    });
}

CrawlerApp.prototype.set_booleans = function() {
    let dbEntry = _websites.findOne({domain: this.domain});
    if (dbEntry) {
        let index = dbEntry.relativePaths.indexOf(myurl.pathname);
        if (index > -1) {
            dbEntry.pathIsCrawled[index] = true;
        }
        index = dbEntry.pathIsCrawled.indexOf(false);
        if (index == -1) {
            dbEntry.isCrawled = true;
        }
        _websites.update(dbEntry);
    }
}

CrawlerApp.prototype.filter_links = function(input) {
    let external = [];
    let internal = [];
    for (let i = 0; i < input.length; i++) {
        let deconstruction = new URL(input[i],this.currentLink);
        if (deconstruction.host === this.domain) {
            if (deconstruction.path == '') {
                // skip
            }
            else {
                internal.push({
                    protocol: deconstruction.protocol,
                    domain: deconstruction.host,
                    path: deconstruction.pathname,
                    href: deconstruction.href
                });
            }
        }
        else {
            external.push({
                protocol: deconstruction.protocol,
                domain: deconstruction.host,
                path: deconstruction.pathname,
                href: deconstruction.href
            });
        }
    }
    return {internal, external};
}

CrawlerApp.prototype.process_request = function(body) {
    var $ = cheerio.load(body)
    let anchors = $('a');
    let linkList = [];
    for (let i = 0; i < anchors.length; i++) {
        let href = anchors[i].attribs.href;
        if (href != undefined && href != null) {
            linkList.push(href);
        }
    }
    return linkList;
}

// construct database element
CrawlerApp.prototype.DBelement = function(protocol, href, isCrawled = false) {
    let obj = {};
    obj.protocol = protocol; // https ?
    obj.domain = this.domain; // host / hostname
    obj.href = href; // full url
    obj.relativePaths = []; // Array containing all child paths of the domain
    obj.pathIsCrawled = []; // Array containing boolean for each relative path
    obj.isCrawled = isCrawled;
    return obj;
}

CrawlerApp.prototype.store_unique = function(input) {
    let {internal, external} = input;
    let index = -1;
    let dbEntryInternal = _websites.findOne({domain: this.domain});
    // add relative path to DB if new
    for (let i = 0; i < internal.length; i++) {
        index = dbEntryInternal.relativePaths.indexOf(internal[i].path);
        if (index == -1) {
            dbEntryInternal.relativePaths.push(internal[i].path);
            dbEntryInternal.pathIsCrawled.push(false);
        }
    }
    _websites.update(dbEntryInternal);

    // add new external websites to DB
    for (let i = 0; i < external.length; i++) {
        let dbEntry = _websites.findOne({domain: external[i].domain});
        // create website in DB if new
        if (!dbEntry) {
            let href = this.createHref(external[i]);
            let el = this.DBelement(external[i].protocol, href, false);
            if (external[i].path > "") {
                el.relativePaths.push(external[i].path);
                el.pathIsCrawled.push(false);
            }
            _websites.insert(el);
        }
        // add relative path to DB if new
        else {
            index = dbEntry.relativePaths.indexOf(external[i].path);
            if (index == -1) {
                dbEntry.relativePaths.push(external[i].path);
                dbEntry.pathIsCrawled.push(false);
                _websites.update(dbEntry);
            }
        }
    }
    return true;
}

// create href from obj with properties protocol and domain
CrawlerApp.prototype.createHref = function(obj) {
    let href = ""+obj.protocol;
    href += (href.length > 0)? "//":"";
    href += obj.domain;
    return href;
}



