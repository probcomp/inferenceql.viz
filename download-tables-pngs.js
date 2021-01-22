const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch({headless: true});
  const page = await browser.newPage();
  await page.setViewport({
    width: 1680,
    height: 1000,
    // should I use this scale factor
    deviceScaleFactor: 2,
  });

  await page._client.send('Page.setDownloadBehavior', {
    behavior: 'allow',
    downloadPath: 'table-pngs/'
  })
  // what is networkidle2
  await page.goto('http://localhost:9500', {waitUntil: 'networkidle2'});
  //await page.screenshot({path: 'example.png'});

  await browser.close();
})();
