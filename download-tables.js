const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  await page.setViewport({
    width: 1024,
    height: 900,
    // should I use this scale factor
    deviceScaleFactor: 2,
  });
  // what is networkidle2
  await page.goto('http://localhost:9500', {waitUntil: 'networkidle2'});
  await page.screenshot({path: 'example.png'});

  await browser.close();
})();