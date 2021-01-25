const puppeteer = require('puppeteer');

var iql_app_path = "file://" + __dirname + "/index.html";
var png_path = 'table-pngs/';

(async () => {
  const browser = await puppeteer.launch({headless: true});
  const page = await browser.newPage();
  await page.setViewport({
    // Adjust these dimensions of there is clipping on the tables. 
    width: 5000,
    height: 10000,
    // Adjust this to increase or decrease quality of table images. 
    // Multiples of 2 are best. 
    deviceScaleFactor: 2,
  });

  await page._client.send('Page.setDownloadBehavior', {
    behavior: 'allow',
    downloadPath: png_path
  })

  // what is networkidle2
  await page.goto(iql_app_path, {waitUntil: 'networkidle2'});
  await browser.close();
})();
