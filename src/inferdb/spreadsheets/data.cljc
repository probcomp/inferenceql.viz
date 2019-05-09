(ns inferdb.spreadsheets.data)

(def nyt-data
  [{ "district_name" "CD 1, Alabama", "geo_fips" 101, "percap" 25695.0, "percent_college" 0.2404, "percent_black" 0.2724, "percent_married_children" 0.1699}
   { "district_name" "CD 2, Alabama", "geo_fips" 102, "percap" 24161.0, "percent_college" 0.2179, "percent_black" 0.3206, "percent_married_children" 0.1738}
   { "district_name" "CD 3, Alabama", "geo_fips" 103, "percap" 24132.0, "percent_college" 0.2277, "percent_black" 0.2596, "percent_married_children" 0.1998}
   { "district_name" "CD 4, Alabama", "geo_fips" 104, "percap" 22320.0, "percent_college" 0.1703, "percent_black" 0.0692, "percent_married_children" 0.2034}
   { "district_name" "CD 5, Alabama", "geo_fips" 105, "percap" 29758.0, "percent_college" 0.3027, "percent_black" 0.1777, "percent_married_children" 0.1824}
   { "district_name" "CD 6, Alabama", "geo_fips" 106, "percap" 33383.0, "percent_college" 0.3674, "percent_black" 0.1485, "percent_married_children" 0.2219}
   { "district_name" "CD 7, Alabama", "geo_fips" 107, "percap" 20732.0, "percent_college" 0.1935, "percent_black" 0.6329, "percent_married_children" 0.1138}
   { "district_name" "CD (at Large}, Alaska", "geo_fips" 200, "percap" 34187.0, "percent_college" 0.2956, "percent_black" 0.0293, "percent_married_children" 0.2305}
   { "district_name" "CD 1, Arizona", "geo_fips" 401, "percap" 22491.0, "percent_college" 0.2453, "percent_black" 0.0209, "percent_married_children" 0.1753}
   { "district_name" "CD 2, Arizona", "geo_fips" 402, "percap" 28577.0, "percent_college" 0.3396, "percent_black" 0.0368, "percent_married_children" 0.1414}
   { "district_name" "CD 3, Arizona", "geo_fips" 403, "percap" 19232.0, "percent_college" 0.1646, "percent_black" 0.0427, "percent_married_children" 0.234}
   { "district_name" "CD 4, Arizona", "geo_fips" 404, "percap" 26523.0, "percent_college" 0.1917, "percent_black" 0.0158, "percent_married_children" 0.1486}
   { "district_name" "CD 5, Arizona", "geo_fips" 405, "percap" 32918.0, "percent_college" 0.3602, "percent_black" 0.0332, "percent_married_children" 0.2735}
   { "district_name" "CD 6, Arizona", "geo_fips" 406, "percap" 42779.0, "percent_college" 0.454, "percent_black" 0.0207, "percent_married_children" 0.1835}
   { "district_name" "CD 7, Arizona", "geo_fips" 407, "percap" 16959.0, "percent_college" 0.1402, "percent_black" 0.0948, "percent_married_children" 0.2173}
   { "district_name" "CD 8, Arizona", "geo_fips" 408, "percap" 30432.0, "percent_college" 0.2935, "percent_black" 0.0378, "percent_married_children" 0.2129}
   { "district_name" "CD 9, Arizona", "geo_fips" 409, "percap" 32415.0, "percent_college" 0.3755, "percent_black" 0.0622, "percent_married_children" 0.1541}
   { "district_name" "CD 1, Arkansas", "geo_fips" 501, "percap" 21907.0, "percent_college" 0.1646, "percent_black" 0.1771, "percent_married_children" 0.1688}
   { "district_name" "CD 2, Arkansas", "geo_fips" 502, "percap" 27592.0, "percent_college" 0.2997, "percent_black" 0.2224, "percent_married_children" 0.1877}
   { "district_name" "CD 3, Arkansas", "geo_fips" 503, "percap" 25991.0, "percent_college" 0.2688, "percent_black" 0.0301, "percent_married_children" 0.2427}
   { "district_name" "CD 4, Arkansas", "geo_fips" 504, "percap" 21182.0, "percent_college" 0.1581, "percent_black" 0.1988, "percent_married_children" 0.1749}
   { "district_name" "CD 1, California", "geo_fips" 601, "percap" 26949.0, "percent_college" 0.2366, "percent_black" 0.0129, "percent_married_children" 0.1495}
   { "district_name" "CD 2, California", "geo_fips" 602, "percap" 44818.0, "percent_college" 0.4076, "percent_black" 0.0128, "percent_married_children" 0.1789}
   { "district_name" "CD 3, California", "geo_fips" 603, "percap" 29224.0, "percent_college" 0.2508, "percent_black" 0.0603, "percent_married_children" 0.2304}
   { "district_name" "CD 4, California", "geo_fips" 604, "percap" 38757.0, "percent_college" 0.3328, "percent_black" 0.0126, "percent_married_children" 0.2153}
   { "district_name" "CD 5, California", "geo_fips" 605, "percap" 35209.0, "percent_college" 0.3121, "percent_black" 0.059, "percent_married_children" 0.1964}
   { "district_name" "CD 6, California", "geo_fips" 606, "percap" 26642.0, "percent_college" 0.2752, "percent_black" 0.1249, "percent_married_children" 0.1958}
   { "district_name" "CD 7, California", "geo_fips" 607, "percap" 34031.0, "percent_college" 0.3291, "percent_black" 0.0676, "percent_married_children" 0.2319}
   { "district_name" "CD 8, California", "geo_fips" 608, "percap" 21809.0, "percent_college" 0.1709, "percent_black" 0.0866, "percent_married_children" 0.2423}
   { "district_name" "CD 9, California", "geo_fips" 609, "percap" 26494.0, "percent_college" 0.1914, "percent_black" 0.0895, "percent_married_children" 0.2567}
   { "district_name" "CD 10, California", "geo_fips" 610, "percap" 25293.0, "percent_college" 0.1656, "percent_black" 0.0306, "percent_married_children" 0.2877}
   { "district_name" "CD 11, California", "geo_fips" 611, "percap" 44515.0, "percent_college" 0.436, "percent_black" 0.0745, "percent_married_children" 0.2419}
   { "district_name" "CD 12, California", "geo_fips" 612, "percap" 64839.0, "percent_college" 0.5948, "percent_black" 0.0495, "percent_married_children" 0.1324}
   { "district_name" "CD 13, California", "geo_fips" 613, "percap" 41397.0, "percent_college" 0.4715, "percent_black" 0.1683, "percent_married_children" 0.1766}
   { "district_name" "CD 14, California", "geo_fips" 614, "percap" 50487.0, "percent_college" 0.4753, "percent_black" 0.025, "percent_married_children" 0.2447}
   { "district_name" "CD 15, California", "geo_fips" 615, "percap" 44491.0, "percent_college" 0.4499, "percent_black" 0.049, "percent_married_children" 0.3153}
   { "district_name" "CD 16, California", "geo_fips" 616, "percap" 18362.0, "percent_college" 0.1216, "percent_black" 0.0604, "percent_married_children" 0.2339}
   { "district_name" "CD 17, California", "geo_fips" 617, "percap" 49171.0, "percent_college" 0.5702, "percent_black" 0.027, "percent_married_children" 0.339}
   { "district_name" "CD 18, California", "geo_fips" 618, "percap" 66772.0, "percent_college" 0.6153, "percent_black" 0.019, "percent_married_children" 0.2697}
   { "district_name" "CD 19, California", "geo_fips" 619, "percap" 36864.0, "percent_college" 0.3553, "percent_black" 0.0256, "percent_married_children" 0.2856}
   { "district_name" "CD 20, California", "geo_fips" 620, "percap" 29672.0, "percent_college" 0.2786, "percent_black" 0.0178, "percent_married_children" 0.2387}
   { "district_name" "CD 21, California", "geo_fips" 621, "percap" 15153.0, "percent_college" 0.0786, "percent_black" 0.0358, "percent_married_children" 0.3503}
   { "district_name" "CD 22, California", "geo_fips" 622, "percap" 25763.0, "percent_college" 0.2411, "percent_black" 0.022, "percent_married_children" 0.2601}
   { "district_name" "CD 23, California", "geo_fips" 623, "percap" 25392.0, "percent_college" 0.2083, "percent_black" 0.0606, "percent_married_children" 0.2309}
   { "district_name" "CD 24, California", "geo_fips" 624, "percap" 32445.0, "percent_college" 0.3403, "percent_black" 0.0174, "percent_married_children" 0.2072}
   { "district_name" "CD 25, California", "geo_fips" 625, "percap" 31565.0, "percent_college" 0.2779, "percent_black" 0.0685, "percent_married_children" 0.2711}
   { "district_name" "CD 26, California", "geo_fips" 626, "percap" 34606.0, "percent_college" 0.3412, "percent_black" 0.0163, "percent_married_children" 0.2414}
   { "district_name" "CD 27, California", "geo_fips" 627, "percap" 35241.0, "percent_college" 0.4052, "percent_black" 0.0474, "percent_married_children" 0.2208}
   { "district_name" "CD 28, California", "geo_fips" 628, "percap" 41930.0, "percent_college" 0.4622, "percent_black" 0.0304, "percent_married_children" 0.1428}
   { "district_name" "CD 29, California", "geo_fips" 629, "percap" 21309.0, "percent_college" 0.1924, "percent_black" 0.0303, "percent_married_children" 0.2481}
   { "district_name" "CD 30, California", "geo_fips" 630, "percap" 41473.0, "percent_college" 0.4245, "percent_black" 0.0566, "percent_married_children" 0.1989}
   { "district_name" "CD 31, California", "geo_fips" 631, "percap" 23514.0, "percent_college" 0.2266, "percent_black" 0.097, "percent_married_children" 0.2482}
   { "district_name" "CD 32, California", "geo_fips" 632, "percap" 23237.0, "percent_college" 0.2136, "percent_black" 0.0293, "percent_married_children" 0.259}
   { "district_name" "CD 33, California", "geo_fips" 633, "percap" 69375.0, "percent_college" 0.6538, "percent_black" 0.0308, "percent_married_children" 0.1776}
   { "district_name" "CD 34, California", "geo_fips" 634, "percap" 21540.0, "percent_college" 0.254, "percent_black" 0.0429, "percent_married_children" 0.1589}
   { "district_name" "CD 35, California", "geo_fips" 635, "percap" 19394.0, "percent_college" 0.1606, "percent_black" 0.0588, "percent_married_children" 0.305}
   { "district_name" "CD 36, California", "geo_fips" 636, "percap" 25180.0, "percent_college" 0.2062, "percent_black" 0.0429, "percent_married_children" 0.177}
   { "district_name" "CD 37, California", "geo_fips" 637, "percap" 33653.0, "percent_college" 0.3689, "percent_black" 0.2135, "percent_married_children" 0.1471}
   { "district_name" "CD 38, California", "geo_fips" 638, "percap" 25489.0, "percent_college" 0.2349, "percent_black" 0.0437, "percent_married_children" 0.265}
   { "district_name" "CD 39, California", "geo_fips" 639, "percap" 35092.0, "percent_college" 0.4162, "percent_black" 0.0228, "percent_married_children" 0.2833}
   { "district_name" "CD 40, California", "geo_fips" 640, "percap" 15885.0, "percent_college" 0.0981, "percent_black" 0.0409, "percent_married_children" 0.289}
   { "district_name" "CD 41, California", "geo_fips" 641, "percap" 21233.0, "percent_college" 0.1827, "percent_black" 0.091, "percent_married_children" 0.2965}
   { "district_name" "CD 42, California", "geo_fips" 642, "percap" 29264.0, "percent_college" 0.2505, "percent_black" 0.0474, "percent_married_children" 0.3072}
   { "district_name" "CD 43, California", "geo_fips" 643, "percap" 26562.0, "percent_college" 0.2654, "percent_black" 0.2125, "percent_married_children" 0.2044}
   { "district_name" "CD 44, California", "geo_fips" 644, "percap" 19009.0, "percent_college" 0.1234, "percent_black" 0.1475, "percent_married_children" 0.2633}
   { "district_name" "CD 45, California", "geo_fips" 645, "percap" 48000.0, "percent_college" 0.5595, "percent_black" 0.0143, "percent_married_children" 0.2687}
   { "district_name" "CD 46, California", "geo_fips" 646, "percap" 21018.0, "percent_college" 0.1869, "percent_black" 0.0156, "percent_married_children" 0.3013}
   { "district_name" "CD 47, California", "geo_fips" 647, "percap" 30348.0, "percent_college" 0.32, "percent_black" 0.0734, "percent_married_children" 0.2174}
   { "district_name" "CD 48, California", "geo_fips" 648, "percap" 47277.0, "percent_college" 0.4456, "percent_black" 0.0128, "percent_married_children" 0.2144}
   { "district_name" "CD 49, California", "geo_fips" 649, "percap" 42826.0, "percent_college" 0.4402, "percent_black" 0.0214, "percent_married_children" 0.2621}
   { "district_name" "CD 50, California", "geo_fips" 650, "percap" 30315.0, "percent_college" 0.2816, "percent_black" 0.0328, "percent_married_children" 0.2695}
   { "district_name" "CD 51, California", "geo_fips" 651, "percap" 18206.0, "percent_college" 0.1412, "percent_black" 0.0646, "percent_married_children" 0.2532}
   { "district_name" "CD 52, California", "geo_fips" 652, "percap" 46537.0, "percent_college" 0.5741, "percent_black" 0.0264, "percent_married_children" 0.2192}
   { "district_name" "CD 53, California", "geo_fips" 653, "percap" 33570.0, "percent_college" 0.3784, "percent_black" 0.0761, "percent_married_children" 0.2051}
   { "district_name" "CD 1, Colorado", "geo_fips" 801, "percap" 39756.0, "percent_college" 0.4764, "percent_black" 0.0811, "percent_married_children" 0.1711}
   { "district_name" "CD 2, Colorado", "geo_fips" 802, "percap" 39981.0, "percent_college" 0.5425, "percent_black" 0.0088, "percent_married_children" 0.2054}
   { "district_name" "CD 3, Colorado", "geo_fips" 803, "percap" 27411.0, "percent_college" 0.2924, "percent_black" 0.008, "percent_married_children" 0.1854}
   { "district_name" "CD 4, Colorado", "geo_fips" 804, "percap" 33787.0, "percent_college" 0.3494, "percent_black" 0.0131, "percent_married_children" 0.2833}
   { "district_name" "CD 5, Colorado", "geo_fips" 805, "percap" 30613.0, "percent_college" 0.37, "percent_black" 0.0562, "percent_married_children" 0.2256}
   { "district_name" "CD 6, Colorado", "geo_fips" 806, "percap" 37219.0, "percent_college" 0.4183, "percent_black" 0.0915, "percent_married_children" 0.2717}
   { "district_name" "CD 7, Colorado", "geo_fips" 807, "percap" 32226.0, "percent_college" 0.3247, "percent_black" 0.0161, "percent_married_children" 0.2023}
   { "district_name" "CD 1, Connecticut", "geo_fips" 901, "percap" 36544.0, "percent_college" 0.3678, "percent_black" 0.1475, "percent_married_children" 0.194}
   { "district_name" "CD 2, Connecticut", "geo_fips" 902, "percap" 37675.0, "percent_college" 0.3512, "percent_black" 0.0373, "percent_married_children" 0.176}
   { "district_name" "CD 3, Connecticut", "geo_fips" 903, "percap" 36056.0, "percent_college" 0.3615, "percent_black" 0.1319, "percent_married_children" 0.16}
   { "district_name" "CD 4, Connecticut", "geo_fips" 904, "percap" 56020.0, "percent_college" 0.4885, "percent_black" 0.1154, "percent_married_children" 0.2508}
   { "district_name" "CD 5, Connecticut", "geo_fips" 905, "percap" 38709.0, "percent_college" 0.363, "percent_black" 0.059, "percent_married_children" 0.1742}
   { "district_name" "CD (at Large}, Delaware", "geo_fips" 1000, "percap" 31712.0, "percent_college" 0.3095, "percent_black" 0.2135, "percent_married_children" 0.1691}
   { "district_name" "CD 1, Florida", "geo_fips" 1201, "percap" 28149.0, "percent_college" 0.2789, "percent_black" 0.1362, "percent_married_children" 0.1782}
   { "district_name" "CD 2, Florida", "geo_fips" 1202, "percap" 25110.0, "percent_college" 0.2339, "percent_black" 0.1263, "percent_married_children" 0.1649}
   { "district_name" "CD 3, Florida", "geo_fips" 1203, "percap" 24072.0, "percent_college" 0.2586, "percent_black" 0.1493, "percent_married_children" 0.1652}
   { "district_name" "CD 4, Florida", "geo_fips" 1204, "percap" 36749.0, "percent_college" 0.3922, "percent_black" 0.0862, "percent_married_children" 0.1997}
   { "district_name" "CD 5, Florida", "geo_fips" 1205, "percap" 20606.0, "percent_college" 0.2141, "percent_black" 0.4718, "percent_married_children" 0.1449}
   { "district_name" "CD 6, Florida", "geo_fips" 1206, "percap" 26083.0, "percent_college" 0.2389, "percent_black" 0.0974, "percent_married_children" 0.1384}
   { "district_name" "CD 7, Florida", "geo_fips" 1207, "percap" 30651.0, "percent_college" 0.3775, "percent_black" 0.0921, "percent_married_children" 0.186}
   { "district_name" "CD 8, Florida", "geo_fips" 1208, "percap" 29605.0, "percent_college" 0.2898, "percent_black" 0.0911, "percent_married_children" 0.1381}
   { "district_name" "CD 9, Florida", "geo_fips" 1209, "percap" 22124.0, "percent_college" 0.2465, "percent_black" 0.1236, "percent_married_children" 0.2179}
   { "district_name" "CD 10, Florida", "geo_fips" 1210, "percap" 27055.0, "percent_college" 0.3033, "percent_black" 0.2723, "percent_married_children" 0.1866}
   { "district_name" "CD 11, Florida", "geo_fips" 1211, "percap" 25513.0, "percent_college" 0.2056, "percent_black" 0.0783, "percent_married_children" 0.0971}
   { "district_name" "CD 12, Florida", "geo_fips" 1212, "percap" 29214.0, "percent_college" 0.2689, "percent_black" 0.0452, "percent_married_children" 0.1614}
   { "district_name" "CD 13, Florida", "geo_fips" 1213, "percap" 30900.0, "percent_college" 0.2779, "percent_black" 0.1199, "percent_married_children" 0.1163}
   { "district_name" "CD 14, Florida", "geo_fips" 1214, "percap" 31648.0, "percent_college" 0.3526, "percent_black" 0.1826, "percent_married_children" 0.1636}
   { "district_name" "CD 15, Florida", "geo_fips" 1215, "percap" 25401.0, "percent_college" 0.2575, "percent_black" 0.1307, "percent_married_children" 0.2019}
   { "district_name" "CD 16, Florida", "geo_fips" 1216, "percap" 31916.0, "percent_college" 0.3072, "percent_black" 0.0746, "percent_married_children" 0.1457}
   { "district_name" "CD 17, Florida", "geo_fips" 1217, "percap" 26354.0, "percent_college" 0.2126, "percent_black" 0.0723, "percent_married_children" 0.1289}
   { "district_name" "CD 18, Florida", "geo_fips" 1218, "percap" 34411.0, "percent_college" 0.3049, "percent_black" 0.1212, "percent_married_children" 0.1502}
   { "district_name" "CD 19, Florida", "geo_fips" 1219, "percap" 35227.0, "percent_college" 0.3232, "percent_black" 0.0782, "percent_married_children" 0.1201}
   { "district_name" "CD 20, Florida", "geo_fips" 1220, "percap" 20374.0, "percent_college" 0.197, "percent_black" 0.52, "percent_married_children" 0.1766}
   { "district_name" "CD 21, Florida", "geo_fips" 1221, "percap" 34037.0, "percent_college" 0.3427, "percent_black" 0.1581, "percent_married_children" 0.1604}
   { "district_name" "CD 22, Florida", "geo_fips" 1222, "percap" 40187.0, "percent_college" 0.3787, "percent_black" 0.1547, "percent_married_children" 0.1533}
   { "district_name" "CD 23, Florida", "geo_fips" 1223, "percap" 34835.0, "percent_college" 0.3858, "percent_black" 0.1239, "percent_married_children" 0.2008}
   { "district_name" "CD 24, Florida", "geo_fips" 1224, "percap" 19669.0, "percent_college" 0.1936, "percent_black" 0.4684, "percent_married_children" 0.1447}
   { "district_name" "CD 25, Florida", "geo_fips" 1225, "percap" 22975.0, "percent_college" 0.2274, "percent_black" 0.0337, "percent_married_children" 0.2115}
   { "district_name" "CD 26, Florida", "geo_fips" 1226, "percap" 22747.0, "percent_college" 0.2722, "percent_black" 0.1024, "percent_married_children" 0.2311}
   { "district_name" "CD 27, Florida", "geo_fips" 1227, "percap" 37681.0, "percent_college" 0.3747, "percent_black" 0.0376, "percent_married_children" 0.1681}
   { "district_name" "CD 1, Georgia", "geo_fips" 1301, "percap" 26644.0, "percent_college" 0.25, "percent_black" 0.2931, "percent_married_children" 0.1926}
   { "district_name" "CD 2, Georgia", "geo_fips" 1302, "percap" 19804.0, "percent_college" 0.1763, "percent_black" 0.5157, "percent_married_children" 0.1508}
   { "district_name" "CD 3, Georgia", "geo_fips" 1303, "percap" 28475.0, "percent_college" 0.2647, "percent_black" 0.244, "percent_married_children" 0.2211}
   { "district_name" "CD 4, Georgia", "geo_fips" 1304, "percap" 25426.0, "percent_college" 0.2985, "percent_black" 0.5853, "percent_married_children" 0.184}
   { "district_name" "CD 5, Georgia", "geo_fips" 1305, "percap" 33008.0, "percent_college" 0.4202, "percent_black" 0.5718, "percent_married_children" 0.1}
   { "district_name" "CD 6, Georgia", "geo_fips" 1306, "percap" 46304.0, "percent_college" 0.6059, "percent_black" 0.1468, "percent_married_children" 0.2694}
   { "district_name" "CD 7, Georgia", "geo_fips" 1307, "percap" 31614.0, "percent_college" 0.3964, "percent_black" 0.202, "percent_married_children" 0.3222}
   { "district_name" "CD 8, Georgia", "geo_fips" 1308, "percap" 22991.0, "percent_college" 0.2178, "percent_black" 0.3067, "percent_married_children" 0.1984}
   { "district_name" "CD 9, Georgia", "geo_fips" 1309, "percap" 23963.0, "percent_college" 0.2158, "percent_black" 0.0717, "percent_married_children" 0.215}
   { "district_name" "CD 10, Georgia", "geo_fips" 1310, "percap" 25544.0, "percent_college" 0.2666, "percent_black" 0.2484, "percent_married_children" 0.2133}
   { "district_name" "CD 11, Georgia", "geo_fips" 1311, "percap" 36404.0, "percent_college" 0.4098, "percent_black" 0.1602, "percent_married_children" 0.2464}
   { "district_name" "CD 12, Georgia", "geo_fips" 1312, "percap" 22374.0, "percent_college" 0.2088, "percent_black" 0.3517, "percent_married_children" 0.1927}
   { "district_name" "CD 13, Georgia", "geo_fips" 1313, "percap" 25950.0, "percent_college" 0.2962, "percent_black" 0.5812, "percent_married_children" 0.2074}
   { "district_name" "CD 14, Georgia", "geo_fips" 1314, "percap" 23855.0, "percent_college" 0.1811, "percent_black" 0.0888, "percent_married_children" 0.2394}
   { "district_name" "CD 1, Hawaii", "geo_fips" 1501, "percap" 34669.0, "percent_college" 0.3602, "percent_black" 0.02, "percent_married_children" 0.2251}
   { "district_name" "CD 2, Hawaii", "geo_fips" 1502, "percap" 30643.0, "percent_college" 0.278, "percent_black" 0.0142, "percent_married_children" 0.2201}
   { "district_name" "CD 1, Idaho", "geo_fips" 1601, "percap" 25361.0, "percent_college" 0.2542, "percent_black" 0.0034, "percent_married_children" 0.2056}
   { "district_name" "CD 2, Idaho", "geo_fips" 1602, "percap" 26014.0, "percent_college" 0.3008, "percent_black" 0.0088, "percent_married_children" 0.2379}
   { "district_name" "CD 1, Illinois", "geo_fips" 1701, "percap" 28130.0, "percent_college" 0.2892, "percent_black" 0.4983, "percent_married_children" 0.1512}
   { "district_name" "CD 2, Illinois", "geo_fips" 1702, "percap" 24297.0, "percent_college" 0.2334, "percent_black" 0.5662, "percent_married_children" 0.1319}
   { "district_name" "CD 3, Illinois", "geo_fips" 1703, "percap" 29713.0, "percent_college" 0.2693, "percent_black" 0.0516, "percent_married_children" 0.2475}
   { "district_name" "CD 4, Illinois", "geo_fips" 1704, "percap" 23939.0, "percent_college" 0.245, "percent_black" 0.0328, "percent_married_children" 0.2273}
   { "district_name" "CD 5, Illinois", "geo_fips" 1705, "percap" 48869.0, "percent_college" 0.5469, "percent_black" 0.0236, "percent_married_children" 0.1878}
   { "district_name" "CD 6, Illinois", "geo_fips" 1706, "percap" 46654.0, "percent_college" 0.5219, "percent_black" 0.0252, "percent_married_children" 0.2697}
   { "district_name" "CD 7, Illinois", "geo_fips" 1707, "percap" 37667.0, "percent_college" 0.4068, "percent_black" 0.4683, "percent_married_children" 0.1015}
   { "district_name" "CD 8, Illinois", "geo_fips" 1708, "percap" 30276.0, "percent_college" 0.3258, "percent_black" 0.0449, "percent_married_children" 0.2375}
   { "district_name" "CD 9, Illinois", "geo_fips" 1709, "percap" 41955.0, "percent_college" 0.5317, "percent_black" 0.0989, "percent_married_children" 0.1963}
   { "district_name" "CD 10, Illinois", "geo_fips" 1710, "percap" 40667.0, "percent_college" 0.4406, "percent_black" 0.0665, "percent_married_children" 0.2736}
   { "district_name" "CD 11, Illinois", "geo_fips" 1711, "percap" 32678.0, "percent_college" 0.3601, "percent_black" 0.104, "percent_married_children" 0.2591}
   { "district_name" "CD 12, Illinois", "geo_fips" 1712, "percap" 26882.0, "percent_college" 0.2395, "percent_black" 0.1694, "percent_married_children" 0.1654}
   { "district_name" "CD 13, Illinois", "geo_fips" 1713, "percap" 27673.0, "percent_college" 0.3024, "percent_black" 0.1098, "percent_married_children" 0.1568}
   { "district_name" "CD 14, Illinois", "geo_fips" 1714, "percap" 37982.0, "percent_college" 0.421, "percent_black" 0.0343, "percent_married_children" 0.3157}
   { "district_name" "CD 15, Illinois", "geo_fips" 1715, "percap" 25860.0, "percent_college" 0.202, "percent_black" 0.0443, "percent_married_children" 0.1948}
   { "district_name" "CD 16, Illinois", "geo_fips" 1716, "percap" 28866.0, "percent_college" 0.2218, "percent_black" 0.0333, "percent_married_children" 0.1958}
   { "district_name" "CD 17, Illinois", "geo_fips" 1717, "percap" 24700.0, "percent_college" 0.1868, "percent_black" 0.113, "percent_married_children" 0.1523}
   { "district_name" "CD 18, Illinois", "geo_fips" 1718, "percap" 32668.0, "percent_college" 0.3168, "percent_black" 0.0402, "percent_married_children" 0.2072}
   { "district_name" "CD 1, Indiana", "geo_fips" 1801, "percap" 28232.0, "percent_college" 0.2261, "percent_black" 0.1853, "percent_married_children" 0.1851}
   { "district_name" "CD 2, Indiana", "geo_fips" 1802, "percap" 24293.0, "percent_college" 0.2077, "percent_black" 0.0689, "percent_married_children" 0.2059}
   { "district_name" "CD 3, Indiana", "geo_fips" 1803, "percap" 25709.0, "percent_college" 0.2288, "percent_black" 0.0624, "percent_married_children" 0.2002}
   { "district_name" "CD 4, Indiana", "geo_fips" 1804, "percap" 27319.0, "percent_college" 0.2598, "percent_black" 0.0418, "percent_married_children" 0.2146}
   { "district_name" "CD 5, Indiana", "geo_fips" 1805, "percap" 38140.0, "percent_college" 0.4496, "percent_black" 0.0726, "percent_married_children" 0.2271}
   { "district_name" "CD 6, Indiana", "geo_fips" 1806, "percap" 25803.0, "percent_college" 0.2072, "percent_black" 0.0261, "percent_married_children" 0.1953}
   { "district_name" "CD 7, Indiana", "geo_fips" 1807, "percap" 23208.0, "percent_college" 0.2346, "percent_black" 0.3033, "percent_married_children" 0.1488}
   { "district_name" "CD 8, Indiana", "geo_fips" 1808, "percap" 25987.0, "percent_college" 0.2128, "percent_black" 0.0385, "percent_married_children" 0.1903}
   { "district_name" "CD 9, Indiana", "geo_fips" 1809, "percap" 28010.0, "percent_college" 0.2635, "percent_black" 0.0256, "percent_married_children" 0.2018}
   { "district_name" "CD 1, Iowa", "geo_fips" 1901, "percap" 30633.0, "percent_college" 0.2708, "percent_black" 0.0379, "percent_married_children" 0.2068}
   { "district_name" "CD 2, Iowa", "geo_fips" 1902, "percap" 27917.0, "percent_college" 0.2846, "percent_black" 0.0441, "percent_married_children" 0.1899}
   { "district_name" "CD 3, Iowa", "geo_fips" 1903, "percap" 33074.0, "percent_college" 0.331, "percent_black" 0.0412, "percent_married_children" 0.2257}
   { "district_name" "CD 4, Iowa", "geo_fips" 1904, "percap" 28357.0, "percent_college" 0.2465, "percent_black" 0.0144, "percent_married_children" 0.1944}
   { "district_name" "CD 1, Kansas", "geo_fips" 2001, "percap" 24350.0, "percent_college" 0.2389, "percent_black" 0.0282, "percent_married_children" 0.2015}
   { "district_name" "CD 2, Kansas", "geo_fips" 2002, "percap" 26783.0, "percent_college" 0.2921, "percent_black" 0.0483, "percent_married_children" 0.1907}
   { "district_name" "CD 3, Kansas", "geo_fips" 2003, "percap" 37433.0, "percent_college" 0.4736, "percent_black" 0.0806, "percent_married_children" 0.2399}
   { "district_name" "CD 4, Kansas", "geo_fips" 2004, "percap" 26686.0, "percent_college" 0.2947, "percent_black" 0.0625, "percent_married_children" 0.2007}
   { "district_name" "CD 1, Kentucky", "geo_fips" 2101, "percap" 22856.0, "percent_college" 0.1568, "percent_black" 0.0759, "percent_married_children" 0.1861}
   { "district_name" "CD 2, Kentucky", "geo_fips" 2102, "percap" 25750.0, "percent_college" 0.2088, "percent_black" 0.0563, "percent_married_children" 0.2057}
   { "district_name" "CD 3, Kentucky", "geo_fips" 2103, "percap" 30288.0, "percent_college" 0.309, "percent_black" 0.2169, "percent_married_children" 0.1509}
   { "district_name" "CD 4, Kentucky", "geo_fips" 2104, "percap" 30526.0, "percent_college" 0.2829, "percent_black" 0.034, "percent_married_children" 0.2269}
   { "district_name" "CD 5, Kentucky", "geo_fips" 2105, "percap" 18575.0, "percent_college" 0.1243, "percent_black" 0.0147, "percent_married_children" 0.1873}
   { "district_name" "CD 6, Kentucky", "geo_fips" 2106, "percap" 27670.0, "percent_college" 0.3166, "percent_black" 0.0872, "percent_married_children" 0.1866}
   { "district_name" "CD 1, Louisiana", "geo_fips" 2201, "percap" 31238.0, "percent_college" 0.3033, "percent_black" 0.1385, "percent_married_children" 0.2039}
   { "district_name" "CD 2, Louisiana", "geo_fips" 2202, "percap" 23818.0, "percent_college" 0.2344, "percent_black" 0.6114, "percent_married_children" 0.1286}
   { "district_name" "CD 3, Louisiana", "geo_fips" 2203, "percap" 24365.0, "percent_college" 0.2101, "percent_black" 0.2505, "percent_married_children" 0.1815}
   { "district_name" "CD 4, Louisiana", "geo_fips" 2204, "percap" 22832.0, "percent_college" 0.1988, "percent_black" 0.347, "percent_married_children" 0.1711}
   { "district_name" "CD 5, Louisiana", "geo_fips" 2205, "percap" 20613.0, "percent_college" 0.1677, "percent_black" 0.344, "percent_married_children" 0.1636}
   { "district_name" "CD 6, Louisiana", "geo_fips" 2206, "percap" 30571.0, "percent_college" 0.2793, "percent_black" 0.2398, "percent_married_children" 0.2032}
   { "district_name" "CD 1, Maine", "geo_fips" 2301, "percap" 33540.0, "percent_college" 0.3694, "percent_black" 0.0177, "percent_married_children" 0.167}
   { "district_name" "CD 2, Maine", "geo_fips" 2302, "percap" 25544.0, "percent_college" 0.2302, "percent_black" 0.0092, "percent_married_children" 0.147}
   { "district_name" "CD 1, Maryland", "geo_fips" 2401, "percap" 34345.0, "percent_college" 0.3056, "percent_black" 0.1191, "percent_married_children" 0.2085}
   { "district_name" "CD 2, Maryland", "geo_fips" 2402, "percap" 32194.0, "percent_college" 0.3338, "percent_black" 0.3362, "percent_married_children" 0.1974}
   { "district_name" "CD 3, Maryland", "geo_fips" 2403, "percap" 43726.0, "percent_college" 0.4716, "percent_black" 0.2175, "percent_married_children" 0.1894}
   { "district_name" "CD 4, Maryland", "geo_fips" 2404, "percap" 35970.0, "percent_college" 0.34, "percent_black" 0.5151, "percent_married_children" 0.1893}
   { "district_name" "CD 5, Maryland", "geo_fips" 2405, "percap" 38916.0, "percent_college" 0.337, "percent_black" 0.3816, "percent_married_children" 0.234}
   { "district_name" "CD 6, Maryland", "geo_fips" 2406, "percap" 37910.0, "percent_college" 0.4148, "percent_black" 0.1386, "percent_married_children" 0.2375}
   { "district_name" "CD 7, Maryland", "geo_fips" 2407, "percap" 35078.0, "percent_college" 0.3782, "percent_black" 0.5252, "percent_married_children" 0.1468}
   { "district_name" "CD 8, Maryland", "geo_fips" 2408, "percap" 50881.0, "percent_college" 0.556, "percent_black" 0.1208, "percent_married_children" 0.253}
   { "district_name" "CD 1, Massachusetts", "geo_fips" 2501, "percap" 29632.0, "percent_college" 0.2973, "percent_black" 0.0561, "percent_married_children" 0.1494}
   { "district_name" "CD 2, Massachusetts", "geo_fips" 2502, "percap" 33892.0, "percent_college" 0.3839, "percent_black" 0.0482, "percent_married_children" 0.1927}
   { "district_name" "CD 3, Massachusetts", "geo_fips" 2503, "percap" 37598.0, "percent_college" 0.3742, "percent_black" 0.0284, "percent_married_children" 0.2186}
   { "district_name" "CD 4, Massachusetts", "geo_fips" 2504, "percap" 49383.0, "percent_college" 0.5102, "percent_black" 0.0324, "percent_married_children" 0.2603}
   { "district_name" "CD 5, Massachusetts", "geo_fips" 2505, "percap" 48695.0, "percent_college" 0.5736, "percent_black" 0.047, "percent_married_children" 0.2245}
   { "district_name" "CD 6, Massachusetts", "geo_fips" 2506, "percap" 42372.0, "percent_college" 0.4398, "percent_black" 0.034, "percent_married_children" 0.2243}
   { "district_name" "CD 7, Massachusetts", "geo_fips" 2507, "percap" 35107.0, "percent_college" 0.4291, "percent_black" 0.2348, "percent_married_children" 0.1199}
   { "district_name" "CD 8, Massachusetts", "geo_fips" 2508, "percap" 43213.0, "percent_college" 0.4683, "percent_black" 0.0925, "percent_married_children" 0.1914}
   { "district_name" "CD 9, Massachusetts", "geo_fips" 2509, "percap" 37633.0, "percent_college" 0.3588, "percent_black" 0.0234, "percent_married_children" 0.1629}
   { "district_name" "CD 1, Michigan", "geo_fips" 2601, "percap" 25936.0, "percent_college" 0.2459, "percent_black" 0.0134, "percent_married_children" 0.1425}
   { "district_name" "CD 2, Michigan", "geo_fips" 2602, "percap" 25522.0, "percent_college" 0.2383, "percent_black" 0.062, "percent_married_children" 0.2071}
   { "district_name" "CD 3, Michigan", "geo_fips" 2603, "percap" 29188.0, "percent_college" 0.3141, "percent_black" 0.0779, "percent_married_children" 0.2178}
   { "district_name" "CD 4, Michigan", "geo_fips" 2604, "percap" 25919.0, "percent_college" 0.2185, "percent_black" 0.0162, "percent_married_children" 0.1755}
   { "district_name" "CD 5, Michigan", "geo_fips" 2605, "percap" 24002.0, "percent_college" 0.1896, "percent_black" 0.1714, "percent_married_children" 0.1584}
   { "district_name" "CD 6, Michigan", "geo_fips" 2606, "percap" 27912.0, "percent_college" 0.2795, "percent_black" 0.0787, "percent_married_children" 0.194}
   { "district_name" "CD 7, Michigan", "geo_fips" 2607, "percap" 29282.0, "percent_college" 0.2465, "percent_black" 0.043, "percent_married_children" 0.1937}
   { "district_name" "CD 8, Michigan", "geo_fips" 2608, "percap" 34603.0, "percent_college" 0.4061, "percent_black" 0.0545, "percent_married_children" 0.2094}
   { "district_name" "CD 9, Michigan", "geo_fips" 2609, "percap" 33233.0, "percent_college" 0.3032, "percent_black" 0.1404, "percent_married_children" 0.1597}
   { "district_name" "CD 10, Michigan", "geo_fips" 2610, "percap" 31019.0, "percent_college" 0.23, "percent_black" 0.0244, "percent_married_children" 0.1978}
   { "district_name" "CD 11, Michigan", "geo_fips" 2611, "percap" 41789.0, "percent_college" 0.4665, "percent_black" 0.0501, "percent_married_children" 0.2363}
   { "district_name" "CD 12, Michigan", "geo_fips" 2612, "percap" 30640.0, "percent_college" 0.3396, "percent_black" 0.1078, "percent_married_children" 0.1733}
   { "district_name" "CD 13, Michigan", "geo_fips" 2613, "percap" 19190.0, "percent_college" 0.1473, "percent_black" 0.5442, "percent_married_children" 0.1133}
   { "district_name" "CD 14, Michigan", "geo_fips" 2614, "percap" 28051.0, "percent_college" 0.3083, "percent_black" 0.5585, "percent_married_children" 0.13}
   { "district_name" "CD 1, Minnesota", "geo_fips" 2701, "percap" 30641.0, "percent_college" 0.2795, "percent_black" 0.0302, "percent_married_children" 0.2095}
   { "district_name" "CD 2, Minnesota", "geo_fips" 2702, "percap" 37188.0, "percent_college" 0.3845, "percent_black" 0.0441, "percent_married_children" 0.2636}
   { "district_name" "CD 3, Minnesota", "geo_fips" 2703, "percap" 46703.0, "percent_college" 0.4901, "percent_black" 0.0821, "percent_married_children" 0.2381}
   { "district_name" "CD 4, Minnesota", "geo_fips" 2704, "percap" 34911.0, "percent_college" 0.4223, "percent_black" 0.0958, "percent_married_children" 0.1986}
   { "district_name" "CD 5, Minnesota", "geo_fips" 2705, "percap" 35517.0, "percent_college" 0.4448, "percent_black" 0.162, "percent_married_children" 0.1535}
   { "district_name" "CD 6, Minnesota", "geo_fips" 2706, "percap" 33682.0, "percent_college" 0.2945, "percent_black" 0.0289, "percent_married_children" 0.2693}
   { "district_name" "CD 7, Minnesota", "geo_fips" 2707, "percap" 27908.0, "percent_college" 0.2183, "percent_black" 0.0105, "percent_married_children" 0.1852}
   { "district_name" "CD 8, Minnesota", "geo_fips" 2708, "percap" 28609.0, "percent_college" 0.2299, "percent_black" 0.0101, "percent_married_children" 0.165}
   { "district_name" "CD 1, Mississippi", "geo_fips" 2801, "percap" 22852.0, "percent_college" 0.2073, "percent_black" 0.2797, "percent_married_children" 0.1993}
   { "district_name" "CD 2, Mississippi", "geo_fips" 2802, "percap" 19401.0, "percent_college" 0.1927, "percent_black" 0.664, "percent_married_children" 0.1421}
   { "district_name" "CD 3, Mississippi", "geo_fips" 2803, "percap" 24646.0, "percent_college" 0.2589, "percent_black" 0.3574, "percent_married_children" 0.194}
   { "district_name" "CD 4, Mississippi", "geo_fips" 2804, "percap" 23654.0, "percent_college" 0.2109, "percent_black" 0.2352, "percent_married_children" 0.183}
   { "district_name" "CD 1, Missouri", "geo_fips" 2901, "percap" 28507.0, "percent_college" 0.3179, "percent_black" 0.4891, "percent_married_children" 0.1141}
   { "district_name" "CD 2, Missouri", "geo_fips" 2902, "percap" 43011.0, "percent_college" 0.4919, "percent_black" 0.0409, "percent_married_children" 0.2344}
   { "district_name" "CD 3, Missouri", "geo_fips" 2903, "percap" 28940.0, "percent_college" 0.264, "percent_black" 0.0336, "percent_married_children" 0.2439}
   { "district_name" "CD 4, Missouri", "geo_fips" 2904, "percap" 24131.0, "percent_college" 0.2393, "percent_black" 0.0465, "percent_married_children" 0.2086}
   { "district_name" "CD 5, Missouri", "geo_fips" 2905, "percap" 28028.0, "percent_college" 0.2821, "percent_black" 0.2202, "percent_married_children" 0.1504}
   { "district_name" "CD 6, Missouri", "geo_fips" 2906, "percap" 28593.0, "percent_college" 0.2865, "percent_black" 0.0382, "percent_married_children" 0.2172}
   { "district_name" "CD 7, Missouri", "geo_fips" 2907, "percap" 24057.0, "percent_college" 0.2316, "percent_black" 0.0185, "percent_married_children" 0.1914}
   { "district_name" "CD 8, Missouri", "geo_fips" 2908, "percap" 21846.0, "percent_college" 0.1504, "percent_black" 0.0463, "percent_married_children" 0.1872}
   { "district_name" "CD (at Large}, Montana", "geo_fips" 3000, "percap" 28933.0, "percent_college" 0.3096, "percent_black" 0.0033, "percent_married_children" 0.1906}
   { "district_name" "CD 1, Nebraska", "geo_fips" 3101, "percap" 29693.0, "percent_college" 0.3247, "percent_black" 0.0279, "percent_married_children" 0.2173}
   { "district_name" "CD 2, Nebraska", "geo_fips" 3102, "percap" 32626.0, "percent_college" 0.3917, "percent_black" 0.0951, "percent_married_children" 0.2251}
   { "district_name" "CD 3, Nebraska", "geo_fips" 3103, "percap" 27180.0, "percent_college" 0.222, "percent_black" 0.0105, "percent_married_children" 0.2004}
   { "district_name" "CD 1, Nevada", "geo_fips" 3201, "percap" 20869.0, "percent_college" 0.1523, "percent_black" 0.1092, "percent_married_children" 0.1507}
   { "district_name" "CD 2, Nevada", "geo_fips" 3202, "percap" 31102.0, "percent_college" 0.2547, "percent_black" 0.0163, "percent_married_children" 0.184}
   { "district_name" "CD 3, Nevada", "geo_fips" 3203, "percap" 34371.0, "percent_college" 0.3148, "percent_black" 0.0647, "percent_married_children" 0.1965}
   { "district_name" "CD 4, Nevada", "geo_fips" 3204, "percap" 25669.0, "percent_college" 0.2043, "percent_black" 0.15, "percent_married_children" 0.1995}
   { "district_name" "CD 1, New Hampshire", "geo_fips" 3301, "percap" 36520.0, "percent_college" 0.3724, "percent_black" 0.0121, "percent_married_children" 0.1864}
   { "district_name" "CD 2, New Hampshire", "geo_fips" 3302, "percap" 36118.0, "percent_college" 0.3593, "percent_black" 0.0116, "percent_married_children" 0.1925}
   { "district_name" "CD 1, New Jersey", "geo_fips" 3401, "percap" 33460.0, "percent_college" 0.3043, "percent_black" 0.1629, "percent_married_children" 0.203}
   { "district_name" "CD 2, New Jersey", "geo_fips" 3402, "percap" 30426.0, "percent_college" 0.258, "percent_black" 0.1162, "percent_married_children" 0.168}
   { "district_name" "CD 3, New Jersey", "geo_fips" 3403, "percap" 37886.0, "percent_college" 0.3385, "percent_black" 0.1039, "percent_married_children" 0.1932}
   { "district_name" "CD 4, New Jersey", "geo_fips" 3404, "percap" 40874.0, "percent_college" 0.3956, "percent_black" 0.0647, "percent_married_children" 0.2349}
   { "district_name" "CD 5, New Jersey", "geo_fips" 3405, "percap" 47162.0, "percent_college" 0.4775, "percent_black" 0.0485, "percent_married_children" 0.2792}
   { "district_name" "CD 6, New Jersey", "geo_fips" 3406, "percap" 34454.0, "percent_college" 0.3887, "percent_black" 0.094, "percent_married_children" 0.2495}
   { "district_name" "CD 7, New Jersey", "geo_fips" 3407, "percap" 55442.0, "percent_college" 0.5132, "percent_black" 0.048, "percent_married_children" 0.2886}
   { "district_name" "CD 8, New Jersey", "geo_fips" 3408, "percap" 33034.0, "percent_college" 0.3311, "percent_black" 0.0903, "percent_married_children" 0.186}
   { "district_name" "CD 9, New Jersey", "geo_fips" 3409, "percap" 33248.0, "percent_college" 0.3363, "percent_black" 0.0903, "percent_married_children" 0.2199}
   { "district_name" "CD 10, New Jersey", "geo_fips" 3410, "percap" 27692.0, "percent_college" 0.2828, "percent_black" 0.4978, "percent_married_children" 0.1792}
   { "district_name" "CD 11, New Jersey", "geo_fips" 3411, "percap" 52661.0, "percent_college" 0.544, "percent_black" 0.0303, "percent_married_children" 0.27}
   { "district_name" "CD 12, New Jersey", "geo_fips" 3412, "percap" 41372.0, "percent_college" 0.4548, "percent_black" 0.1613, "percent_married_children" 0.2425}
   { "district_name" "CD 1, New Mexico", "geo_fips" 3501, "percap" 28106.0, "percent_college" 0.329, "percent_black" 0.0245, "percent_married_children" 0.158}
   { "district_name" "CD 2, New Mexico", "geo_fips" 3502, "percap" 21727.0, "percent_college" 0.2099, "percent_black" 0.0179, "percent_married_children" 0.1932}
   { "district_name" "CD 3, New Mexico", "geo_fips" 3503, "percap" 25561.0, "percent_college" 0.272, "percent_black" 0.0119, "percent_married_children" 0.1731}
   { "district_name" "CD 1, New York", "geo_fips" 3601, "percap" 40163.0, "percent_college" 0.3414, "percent_black" 0.053, "percent_married_children" 0.2479}
   { "district_name" "CD 2, New York", "geo_fips" 3602, "percap" 36835.0, "percent_college" 0.3121, "percent_black" 0.0947, "percent_married_children" 0.2608}
   { "district_name" "CD 3, New York", "geo_fips" 3603, "percap" 53684.0, "percent_college" 0.5165, "percent_black" 0.0262, "percent_married_children" 0.2678}
   { "district_name" "CD 4, New York", "geo_fips" 3604, "percap" 42094.0, "percent_college" 0.4249, "percent_black" 0.1408, "percent_married_children" 0.267}
   { "district_name" "CD 5, New York", "geo_fips" 3605, "percap" 26356.0, "percent_college" 0.2554, "percent_black" 0.4697, "percent_married_children" 0.2074}
   { "district_name" "CD 6, New York", "geo_fips" 3606, "percap" 30194.0, "percent_college" 0.3586, "percent_black" 0.0357, "percent_married_children" 0.2098}
   { "district_name" "CD 7, New York", "geo_fips" 3607, "percap" 30310.0, "percent_college" 0.3369, "percent_black" 0.085, "percent_married_children" 0.1996}
   { "district_name" "CD 8, New York", "geo_fips" 3608, "percap" 29320.0, "percent_college" 0.337, "percent_black" 0.5054, "percent_married_children" 0.1346}
   { "district_name" "CD 9, New York", "geo_fips" 3609, "percap" 30897.0, "percent_college" 0.375, "percent_black" 0.4681, "percent_married_children" 0.1685}
   { "district_name" "CD 10, New York", "geo_fips" 3610, "percap" 69523.0, "percent_college" 0.6026, "percent_black" 0.0309, "percent_married_children" 0.1767}
   { "district_name" "CD 11, New York", "geo_fips" 3611, "percap" 32625.0, "percent_college" 0.3445, "percent_black" 0.0676, "percent_married_children" 0.2633}
   { "district_name" "CD 12, New York", "geo_fips" 3612, "percap" 83122.0, "percent_college" 0.7217, "percent_black" 0.0419, "percent_married_children" 0.1144}
   { "district_name" "CD 13, New York", "geo_fips" 3613, "percap" 24876.0, "percent_college" 0.3098, "percent_black" 0.2431, "percent_married_children" 0.1172}
   { "district_name" "CD 14, New York", "geo_fips" 3614, "percap" 25661.0, "percent_college" 0.256, "percent_black" 0.0937, "percent_married_children" 0.1886}
   { "district_name" "CD 15, New York", "geo_fips" 3615, "percap" 14846.0, "percent_college" 0.1243, "percent_black" 0.2665, "percent_married_children" 0.1399}
   { "district_name" "CD 16, New York", "geo_fips" 3616, "percap" 41543.0, "percent_college" 0.4063, "percent_black" 0.3186, "percent_married_children" 0.1885}
   { "district_name" "CD 17, New York", "geo_fips" 3617, "percap" 44547.0, "percent_college" 0.4446, "percent_black" 0.102, "percent_married_children" 0.2604}
   { "district_name" "CD 18, New York", "geo_fips" 3618, "percap" 38305.0, "percent_college" 0.3572, "percent_black" 0.0855, "percent_married_children" 0.2454}
   { "district_name" "CD 19, New York", "geo_fips" 3619, "percap" 31677.0, "percent_college" 0.2917, "percent_black" 0.0411, "percent_married_children" 0.1663}
   { "district_name" "CD 20, New York", "geo_fips" 3620, "percap" 34756.0, "percent_college" 0.3874, "percent_black" 0.0848, "percent_married_children" 0.1609}
   { "district_name" "CD 21, New York", "geo_fips" 3621, "percap" 27243.0, "percent_college" 0.2321, "percent_black" 0.0275, "percent_married_children" 0.1732}
   { "district_name" "CD 22, New York", "geo_fips" 3622, "percap" 27133.0, "percent_college" 0.2604, "percent_black" 0.0371, "percent_married_children" 0.1659}
   { "district_name" "CD 23, New York", "geo_fips" 3623, "percap" 25547.0, "percent_college" 0.2609, "percent_black" 0.0285, "percent_married_children" 0.1651}
   { "district_name" "CD 24, New York", "geo_fips" 3624, "percap" 29784.0, "percent_college" 0.3028, "percent_black" 0.0801, "percent_married_children" 0.1675}
   { "district_name" "CD 25, New York", "geo_fips" 3625, "percap" 30921.0, "percent_college" 0.373, "percent_black" 0.1504, "percent_married_children" 0.1546}
   { "district_name" "CD 26, New York", "geo_fips" 3626, "percap" 28141.0, "percent_college" 0.3082, "percent_black" 0.173, "percent_married_children" 0.1341}
   { "district_name" "CD 27, New York", "geo_fips" 3627, "percap" 33051.0, "percent_college" 0.3069, "percent_black" 0.0239, "percent_married_children" 0.1881}
   { "district_name" "CD 1, North Carolina", "geo_fips" 3701, "percap" 24035.0, "percent_college" 0.2775, "percent_black" 0.4461, "percent_married_children" 0.1436}
   { "district_name" "CD 2, North Carolina", "geo_fips" 3702, "percap" 31315.0, "percent_college" 0.3623, "percent_black" 0.1971, "percent_married_children" 0.2857}
   { "district_name" "CD 3, North Carolina", "geo_fips" 3703, "percap" 24808.0, "percent_college" 0.228, "percent_black" 0.1908, "percent_married_children" 0.2017}
   { "district_name" "CD 4, North Carolina", "geo_fips" 3704, "percap" 37872.0, "percent_college" 0.5445, "percent_black" 0.2152, "percent_married_children" 0.2186}
   { "district_name" "CD 5, North Carolina", "geo_fips" 3705, "percap" 26338.0, "percent_college" 0.2633, "percent_black" 0.1438, "percent_married_children" 0.1852}
   { "district_name" "CD 6, North Carolina", "geo_fips" 3706, "percap" 26311.0, "percent_college" 0.2463, "percent_black" 0.1984, "percent_married_children" 0.184}
   { "district_name" "CD 7, North Carolina", "geo_fips" 3707, "percap" 26259.0, "percent_college" 0.2488, "percent_black" 0.188, "percent_married_children" 0.1636}
   { "district_name" "CD 8, North Carolina", "geo_fips" 3708, "percap" 25695.0, "percent_college" 0.2651, "percent_black" 0.2283, "percent_married_children" 0.2083}
   { "district_name" "CD 9, North Carolina", "geo_fips" 3709, "percap" 32597.0, "percent_college" 0.3383, "percent_black" 0.1915, "percent_married_children" 0.2486}
   { "district_name" "CD 10, North Carolina", "geo_fips" 3710, "percap" 25925.0, "percent_college" 0.2462, "percent_black" 0.1158, "percent_married_children" 0.1756}
   { "district_name" "CD 11, North Carolina", "geo_fips" 3711, "percap" 25237.0, "percent_college" 0.2398, "percent_black" 0.0339, "percent_married_children" 0.1586}
   { "district_name" "CD 12, North Carolina", "geo_fips" 3712, "percap" 30913.0, "percent_college" 0.3978, "percent_black" 0.3674, "percent_married_children" 0.1924}
   { "district_name" "CD 13, North Carolina", "geo_fips" 3713, "percap" 26904.0, "percent_college" 0.2695, "percent_black" 0.2139, "percent_married_children" 0.1796}
   { "district_name" "CD (at Large}, North Dakota", "geo_fips" 3800, "percap" 33339.0, "percent_college" 0.2956, "percent_black" 0.0248, "percent_married_children" 0.1955}
   { "district_name" "CD 1, Ohio", "geo_fips" 3901, "percap" 31423.0, "percent_college" 0.3383, "percent_black" 0.2175, "percent_married_children" 0.1982}
   { "district_name" "CD 2, Ohio", "geo_fips" 3902, "percap" 32935.0, "percent_college" 0.3234, "percent_black" 0.0837, "percent_married_children" 0.1921}
   { "district_name" "CD 3, Ohio", "geo_fips" 3903, "percap" 24115.0, "percent_college" 0.278, "percent_black" 0.3281, "percent_married_children" 0.1513}
   { "district_name" "CD 4, Ohio", "geo_fips" 3904, "percap" 26004.0, "percent_college" 0.1787, "percent_black" 0.0526, "percent_married_children" 0.1738}
   { "district_name" "CD 5, Ohio", "geo_fips" 3905, "percap" 30424.0, "percent_college" 0.2616, "percent_black" 0.0258, "percent_married_children" 0.1895}
   { "district_name" "CD 6, Ohio", "geo_fips" 3906, "percap" 24818.0, "percent_college" 0.1593, "percent_black" 0.0252, "percent_married_children" 0.1734}
   { "district_name" "CD 7, Ohio", "geo_fips" 3907, "percap" 26745.0, "percent_college" 0.2085, "percent_black" 0.0389, "percent_married_children" 0.2142}
   { "district_name" "CD 8, Ohio", "geo_fips" 3908, "percap" 28539.0, "percent_college" 0.2385, "percent_black" 0.0592, "percent_married_children" 0.199}
   { "district_name" "CD 9, Ohio", "geo_fips" 3909, "percap" 25452.0, "percent_college" 0.2164, "percent_black" 0.164, "percent_married_children" 0.1283}
   { "district_name" "CD 10, Ohio", "geo_fips" 3910, "percap" 28999.0, "percent_college" 0.2794, "percent_black" 0.1681, "percent_married_children" 0.1634}
   { "district_name" "CD 11, Ohio", "geo_fips" 3911, "percap" 26140.0, "percent_college" 0.2683, "percent_black" 0.5227, "percent_married_children" 0.0988}
   { "district_name" "CD 12, Ohio", "geo_fips" 3912, "percap" 36847.0, "percent_college" 0.41, "percent_black" 0.0473, "percent_married_children" 0.2323}
   { "district_name" "CD 13, Ohio", "geo_fips" 3913, "percap" 24583.0, "percent_college" 0.2373, "percent_black" 0.1152, "percent_married_children" 0.1383}
   { "district_name" "CD 14, Ohio", "geo_fips" 3914, "percap" 35365.0, "percent_college" 0.3408, "percent_black" 0.0433, "percent_married_children" 0.2033}
   { "district_name" "CD 15, Ohio", "geo_fips" 3915, "percap" 30410.0, "percent_college" 0.3144, "percent_black" 0.0429, "percent_married_children" 0.2054}
   { "district_name" "CD 16, Ohio", "geo_fips" 3916, "percap" 33452.0, "percent_college" 0.3327, "percent_black" 0.0237, "percent_married_children" 0.2042}
   { "district_name" "CD 1, Oklahoma", "geo_fips" 4001, "percap" 29587.0, "percent_college" 0.3015, "percent_black" 0.0842, "percent_married_children" 0.2005}
   { "district_name" "CD 2, Oklahoma", "geo_fips" 4002, "percap" 20913.0, "percent_college" 0.17, "percent_black" 0.0333, "percent_married_children" 0.1978}
   { "district_name" "CD 3, Oklahoma", "geo_fips" 4003, "percap" 24016.0, "percent_college" 0.2288, "percent_black" 0.0351, "percent_married_children" 0.216}
   { "district_name" "CD 4, Oklahoma", "geo_fips" 4004, "percap" 26796.0, "percent_college" 0.2551, "percent_black" 0.0679, "percent_married_children" 0.2187}
   { "district_name" "CD 5, Oklahoma", "geo_fips" 4005, "percap" 27725.0, "percent_college" 0.301, "percent_black" 0.1341, "percent_married_children" 0.1893}
   { "district_name" "CD 1, Oregon", "geo_fips" 4101, "percap" 36229.0, "percent_college" 0.4106, "percent_black" 0.0142, "percent_married_children" 0.2335}
   { "district_name" "CD 2, Oregon", "geo_fips" 4102, "percap" 26409.0, "percent_college" 0.2382, "percent_black" 0.0073, "percent_married_children" 0.1748}
   { "district_name" "CD 3, Oregon", "geo_fips" 4103, "percap" 33839.0, "percent_college" 0.4119, "percent_black" 0.0521, "percent_married_children" 0.1874}
   { "district_name" "CD 4, Oregon", "geo_fips" 4104, "percap" 26405.0, "percent_college" 0.2674, "percent_black" 0.0073, "percent_married_children" 0.1497}
   { "district_name" "CD 5, Oregon", "geo_fips" 4105, "percap" 30917.0, "percent_college" 0.2997, "percent_black" 0.0103, "percent_married_children" 0.2}
   { "district_name" "CD 1, Rhode Island", "geo_fips" 4401, "percap" 32302.0, "percent_college" 0.342, "percent_black" 0.0791, "percent_married_children" 0.1673}
   { "district_name" "CD 2, Rhode Island", "geo_fips" 4402, "percap" 33739.0, "percent_college" 0.3399, "percent_black" 0.0309, "percent_married_children" 0.1566}
   { "district_name" "CD 1, South Carolina", "geo_fips" 4501, "percap" 35549.0, "percent_college" 0.3986, "percent_black" 0.179, "percent_married_children" 0.192}
   { "district_name" "CD 2, South Carolina", "geo_fips" 4502, "percap" 29799.0, "percent_college" 0.3346, "percent_black" 0.2328, "percent_married_children" 0.2116}
   { "district_name" "CD 3, South Carolina", "geo_fips" 4503, "percap" 24185.0, "percent_college" 0.2199, "percent_black" 0.1777, "percent_married_children" 0.1723}
   { "district_name" "CD 4, South Carolina", "geo_fips" 4504, "percap" 28564.0, "percent_college" 0.3068, "percent_black" 0.1879, "percent_married_children" 0.1822}
   { "district_name" "CD 5, South Carolina", "geo_fips" 4505, "percap" 25936.0, "percent_college" 0.2304, "percent_black" 0.2645, "percent_married_children" 0.1795}
   { "district_name" "CD 6, South Carolina", "geo_fips" 4506, "percap" 20001.0, "percent_college" 0.196, "percent_black" 0.568, "percent_married_children" 0.1243}
   { "district_name" "CD 7, South Carolina", "geo_fips" 4507, "percap" 24069.0, "percent_college" 0.2015, "percent_black" 0.28, "percent_married_children" 0.1557}
   { "district_name" "CD (at Large}, South Dakota", "geo_fips" 4600, "percap" 28585.0, "percent_college" 0.2885, "percent_black" 0.0166, "percent_married_children" 0.2004}
   { "district_name" "CD 1, Tennessee", "geo_fips" 4701, "percap" 23487.0, "percent_college" 0.1942, "percent_black" 0.0185, "percent_married_children" 0.1718}
   { "district_name" "CD 2, Tennessee", "geo_fips" 4702, "percap" 28119.0, "percent_college" 0.3097, "percent_black" 0.0625, "percent_married_children" 0.1875}
   { "district_name" "CD 3, Tennessee", "geo_fips" 4703, "percap" 26075.0, "percent_college" 0.2274, "percent_black" 0.1068, "percent_married_children" 0.1596}
   { "district_name" "CD 4, Tennessee", "geo_fips" 4704, "percap" 25460.0, "percent_college" 0.2255, "percent_black" 0.0917, "percent_married_children" 0.219}
   { "district_name" "CD 5, Tennessee", "geo_fips" 4705, "percap" 32051.0, "percent_college" 0.375, "percent_black" 0.2483, "percent_married_children" 0.1533}
   { "district_name" "CD 6, Tennessee", "geo_fips" 4706, "percap" 26712.0, "percent_college" 0.2192, "percent_black" 0.0446, "percent_married_children" 0.2177}
   { "district_name" "CD 7, Tennessee", "geo_fips" 4707, "percap" 29219.0, "percent_college" 0.2807, "percent_black" 0.095, "percent_married_children" 0.2592}
   { "district_name" "CD 8, Tennessee", "geo_fips" 4708, "percap" 30049.0, "percent_college" 0.2757, "percent_black" 0.194, "percent_married_children" 0.2149}
   { "district_name" "CD 9, Tennessee", "geo_fips" 4709, "percap" 22217.0, "percent_college" 0.2411, "percent_black" 0.6603, "percent_married_children" 0.1077}
   { "district_name" "CD 1, Texas", "geo_fips" 4801, "percap" 22948.0, "percent_college" 0.2087, "percent_black" 0.178, "percent_married_children" 0.2092}
   { "district_name" "CD 2, Texas", "geo_fips" 4802, "percap" 39593.0, "percent_college" 0.4081, "percent_black" 0.1238, "percent_married_children" 0.2283}
   { "district_name" "CD 3, Texas", "geo_fips" 4803, "percap" 41962.0, "percent_college" 0.5352, "percent_black" 0.0963, "percent_married_children" 0.3178}
   { "district_name" "CD 4, Texas", "geo_fips" 4804, "percap" 26686.0, "percent_college" 0.2213, "percent_black" 0.1041, "percent_married_children" 0.2334}
   { "district_name" "CD 5, Texas", "geo_fips" 4805, "percap" 24307.0, "percent_college" 0.2105, "percent_black" 0.1477, "percent_married_children" 0.241}
   { "district_name" "CD 6, Texas", "geo_fips" 4806, "percap" 29087.0, "percent_college" 0.2875, "percent_black" 0.2007, "percent_married_children" 0.2524}
   { "district_name" "CD 7, Texas", "geo_fips" 4807, "percap" 45900.0, "percent_college" 0.5068, "percent_black" 0.1246, "percent_married_children" 0.2463}
   { "district_name" "CD 8, Texas", "geo_fips" 4808, "percap" 32984.0, "percent_college" 0.3015, "percent_black" 0.0861, "percent_married_children" 0.265}
   { "district_name" "CD 9, Texas", "geo_fips" 4809, "percap" 20788.0, "percent_college" 0.2382, "percent_black" 0.3652, "percent_married_children" 0.2015}
   { "district_name" "CD 10, Texas", "geo_fips" 4810, "percap" 36517.0, "percent_college" 0.3871, "percent_black" 0.1062, "percent_married_children" 0.266}
   { "district_name" "CD 11, Texas", "geo_fips" 4811, "percap" 27915.0, "percent_college" 0.2035, "percent_black" 0.037, "percent_married_children" 0.2037}
   { "district_name" "CD 12, Texas", "geo_fips" 4812, "percap" 31952.0, "percent_college" 0.2994, "percent_black" 0.0844, "percent_married_children" 0.2332}
   { "district_name" "CD 13, Texas", "geo_fips" 4813, "percap" 25315.0, "percent_college" 0.2036, "percent_black" 0.049, "percent_married_children" 0.2305}
   { "district_name" "CD 14, Texas", "geo_fips" 4814, "percap" 29325.0, "percent_college" 0.2272, "percent_black" 0.1976, "percent_married_children" 0.2081}
   { "district_name" "CD 15, Texas", "geo_fips" 4815, "percap" 18423.0, "percent_college" 0.1975, "percent_black" 0.0145, "percent_married_children" 0.2972}
   { "district_name" "CD 16, Texas", "geo_fips" 4816, "percap" 20563.0, "percent_college" 0.239, "percent_black" 0.0357, "percent_married_children" 0.2345}
   { "district_name" "CD 17, Texas", "geo_fips" 4817, "percap" 26713.0, "percent_college" 0.2938, "percent_black" 0.1294, "percent_married_children" 0.2055}
   { "district_name" "CD 18, Texas", "geo_fips" 4818, "percap" 24132.0, "percent_college" 0.2242, "percent_black" 0.3544, "percent_married_children" 0.1889}
   { "district_name" "CD 19, Texas", "geo_fips" 4819, "percap" 23579.0, "percent_college" 0.2242, "percent_black" 0.0586, "percent_married_children" 0.212}
   { "district_name" "CD 20, Texas", "geo_fips" 4820, "percap" 22507.0, "percent_college" 0.2472, "percent_black" 0.0497, "percent_married_children" 0.2166}
   { "district_name" "CD 21, Texas", "geo_fips" 4821, "percap" 38034.0, "percent_college" 0.4506, "percent_black" 0.0341, "percent_married_children" 0.1888}
   { "district_name" "CD 22, Texas", "geo_fips" 4822, "percap" 38041.0, "percent_college" 0.4507, "percent_black" 0.132, "percent_married_children" 0.358}
   { "district_name" "CD 23, Texas", "geo_fips" 4823, "percap" 23280.0, "percent_college" 0.2166, "percent_black" 0.0355, "percent_married_children" 0.2714}
   { "district_name" "CD 24, Texas", "geo_fips" 4824, "percap" 40379.0, "percent_college" 0.4475, "percent_black" 0.1145, "percent_married_children" 0.2234}
   { "district_name" "CD 25, Texas", "geo_fips" 4825, "percap" 36139.0, "percent_college" 0.3715, "percent_black" 0.0684, "percent_married_children" 0.2538}
   { "district_name" "CD 26, Texas", "geo_fips" 4826, "percap" 38097.0, "percent_college" 0.4425, "percent_black" 0.0692, "percent_married_children" 0.3229}
   { "district_name" "CD 27, Texas", "geo_fips" 4827, "percap" 26761.0, "percent_college" 0.2025, "percent_black" 0.0529, "percent_married_children" 0.2096}
   { "district_name" "CD 28, Texas", "geo_fips" 4828, "percap" 18843.0, "percent_college" 0.1769, "percent_black" 0.0355, "percent_married_children" 0.2845}
   { "district_name" "CD 29, Texas", "geo_fips" 4829, "percap" 17512.0, "percent_college" 0.1036, "percent_black" 0.1031, "percent_married_children" 0.2731}
   { "district_name" "CD 30, Texas", "geo_fips" 4830, "percap" 21974.0, "percent_college" 0.2053, "percent_black" 0.4304, "percent_married_children" 0.166}
   { "district_name" "CD 31, Texas", "geo_fips" 4831, "percap" 31588.0, "percent_college" 0.3432, "percent_black" 0.1048, "percent_married_children" 0.2681}
   { "district_name" "CD 32, Texas", "geo_fips" 4832, "percap" 42307.0, "percent_college" 0.4339, "percent_black" 0.1418, "percent_married_children" 0.2256}
   { "district_name" "CD 33, Texas", "geo_fips" 4833, "percap" 15556.0, "percent_college" 0.0932, "percent_black" 0.1528, "percent_married_children" 0.2513}
   { "district_name" "CD 34, Texas", "geo_fips" 4834, "percap" 16944.0, "percent_college" 0.1509, "percent_black" 0.0121, "percent_married_children" 0.265}
   { "district_name" "CD 35, Texas", "geo_fips" 4835, "percap" 21480.0, "percent_college" 0.2036, "percent_black" 0.0974, "percent_married_children" 0.2081}
   { "district_name" "CD 36, Texas", "geo_fips" 4836, "percap" 28675.0, "percent_college" 0.1944, "percent_black" 0.0889, "percent_married_children" 0.2308}
   { "district_name" "CD 1, Utah", "geo_fips" 4901, "percap" 26194.0, "percent_college" 0.289, "percent_black" 0.0099, "percent_married_children" 0.3426}
   { "district_name" "CD 2, Utah", "geo_fips" 4902, "percap" 26807.0, "percent_college" 0.3251, "percent_black" 0.0136, "percent_married_children" 0.2633}
   { "district_name" "CD 3, Utah", "geo_fips" 4903, "percap" 28770.0, "percent_college" 0.4161, "percent_black" 0.0046, "percent_married_children" 0.3458}
   { "district_name" "CD 4, Utah", "geo_fips" 4904, "percap" 26246.0, "percent_college" 0.282, "percent_black" 0.0123, "percent_married_children" 0.3397}
   { "district_name" "CD (at Large}, Vermont", "geo_fips" 5000, "percap" 31836.0, "percent_college" 0.3637, "percent_black" 0.0117, "percent_married_children" 0.1644}
   { "district_name" "CD 1, Virginia", "geo_fips" 5101, "percap" 37264.0, "percent_college" 0.3749, "percent_black" 0.1537, "percent_married_children" 0.2617}
   { "district_name" "CD 2, Virginia", "geo_fips" 5102, "percap" 33688.0, "percent_college" 0.3444, "percent_black" 0.1972, "percent_married_children" 0.2035}
   { "district_name" "CD 3, Virginia", "geo_fips" 5103, "percap" 27901.0, "percent_college" 0.2618, "percent_black" 0.4495, "percent_married_children" 0.1411}
   { "district_name" "CD 4, Virginia", "geo_fips" 5104, "percap" 29971.0, "percent_college" 0.3013, "percent_black" 0.408, "percent_married_children" 0.1592}
   { "district_name" "CD 5, Virginia", "geo_fips" 5105, "percap" 28754.0, "percent_college" 0.2794, "percent_black" 0.1966, "percent_married_children" 0.1745}
   { "district_name" "CD 6, Virginia", "geo_fips" 5106, "percap" 27250.0, "percent_college" 0.2664, "percent_black" 0.1111, "percent_married_children" 0.1688}
   { "district_name" "CD 7, Virginia", "geo_fips" 5107, "percap" 35846.0, "percent_college" 0.3896, "percent_black" 0.1693, "percent_married_children" 0.2436}
   { "district_name" "CD 8, Virginia", "geo_fips" 5108, "percap" 54612.0, "percent_college" 0.6176, "percent_black" 0.1375, "percent_married_children" 0.2126}
   { "district_name" "CD 9, Virginia", "geo_fips" 5109, "percap" 24290.0, "percent_college" 0.2036, "percent_black" 0.0536, "percent_married_children" 0.1685}
   { "district_name" "CD 10, Virginia", "geo_fips" 5110, "percap" 49981.0, "percent_college" 0.5433, "percent_black" 0.0655, "percent_married_children" 0.3503}
   { "district_name" "CD 11, Virginia", "geo_fips" 5111, "percap" 45038.0, "percent_college" 0.5548, "percent_black" 0.1313, "percent_married_children" 0.2842}
   { "district_name" "CD 1, Washington", "geo_fips" 5301, "percap" 42593.0, "percent_college" 0.4335, "percent_black" 0.0127, "percent_married_children" 0.2663}
   { "district_name" "CD 2, Washington", "geo_fips" 5302, "percap" 32802.0, "percent_college" 0.3029, "percent_black" 0.0328, "percent_married_children" 0.1933}
   { "district_name" "CD 3, Washington", "geo_fips" 5303, "percap" 29968.0, "percent_college" 0.2543, "percent_black" 0.0121, "percent_married_children" 0.2157}
   { "district_name" "CD 4, Washington", "geo_fips" 5304, "percap" 23991.0, "percent_college" 0.2012, "percent_black" 0.0096, "percent_married_children" 0.2388}
   { "district_name" "CD 5, Washington", "geo_fips" 5305, "percap" 27495.0, "percent_college" 0.3037, "percent_black" 0.0152, "percent_married_children" 0.1879}
   { "district_name" "CD 6, Washington", "geo_fips" 5306, "percap" 32637.0, "percent_college" 0.2972, "percent_black" 0.0363, "percent_married_children" 0.1744}
   { "district_name" "CD 7, Washington", "geo_fips" 5307, "percap" 54859.0, "percent_college" 0.6101, "percent_black" 0.0393, "percent_married_children" 0.1593}
   { "district_name" "CD 8, Washington", "geo_fips" 5308, "percap" 36598.0, "percent_college" 0.3452, "percent_black" 0.0272, "percent_married_children" 0.2691}
   { "district_name" "CD 9, Washington", "geo_fips" 5309, "percap" 40171.0, "percent_college" 0.4177, "percent_black" 0.1056, "percent_married_children" 0.2099}
   { "district_name" "CD 10, Washington", "geo_fips" 5310, "percap" 29976.0, "percent_college" 0.2866, "percent_black" 0.0566, "percent_married_children" 0.2187}
   { "district_name" "CD 1, West Virginia", "geo_fips" 5401, "percap" 26172.0, "percent_college" 0.2346, "percent_black" 0.028, "percent_married_children" 0.1641}
   { "district_name" "CD 2, West Virginia", "geo_fips" 5402, "percap" 26713.0, "percent_college" 0.2146, "percent_black" 0.0486, "percent_married_children" 0.182}
   { "district_name" "CD 3, West Virginia", "geo_fips" 5403, "percap" 21256.0, "percent_college" 0.174, "percent_black" 0.0359, "percent_married_children" 0.171}
   { "district_name" "CD 1, Wisconsin", "geo_fips" 5501, "percap" 32083.0, "percent_college" 0.2819, "percent_black" 0.0525, "percent_married_children" 0.2064}
   { "district_name" "CD 2, Wisconsin", "geo_fips" 5502, "percap" 34919.0, "percent_college" 0.4261, "percent_black" 0.042, "percent_married_children" 0.1944}
   { "district_name" "CD 3, Wisconsin", "geo_fips" 5503, "percap" 27175.0, "percent_college" 0.2543, "percent_black" 0.0119, "percent_married_children" 0.1793}
   { "district_name" "CD 4, Wisconsin", "geo_fips" 5504, "percap" 24156.0, "percent_college" 0.2741, "percent_black" 0.3331, "percent_married_children" 0.1292}
   { "district_name" "CD 5, Wisconsin", "geo_fips" 5505, "percap" 37330.0, "percent_college" 0.3688, "percent_black" 0.0193, "percent_married_children" 0.2099}
   { "district_name" "CD 6, Wisconsin", "geo_fips" 5506, "percap" 31307.0, "percent_college" 0.2639, "percent_black" 0.0159, "percent_married_children" 0.1921}
   { "district_name" "CD 7, Wisconsin", "geo_fips" 5507, "percap" 29417.0, "percent_college" 0.231, "percent_black" 0.0072, "percent_married_children" 0.1727}
   { "district_name" "CD 8, Wisconsin", "geo_fips" 5508, "percap" 30536.0, "percent_college" 0.2543, "percent_black" 0.0133, "percent_married_children" 0.1973}
   { "district_name" "CD (at Large}, Wyoming", "geo_fips" 5600, "percap" 30042.0, "percent_college" 0.271, "percent_black" 0.0092, "percent_married_children" 0.1975}
   { "district_name" "CD 1, Pennsylvania", "geo_fips" 4201, "percap" 38726.5950469, "percent_college" 0.389, "percent_black" 0.0391, "percent_married_children" 0.2403}
   { "district_name" "CD 2, Pennsylvania", "geo_fips" 4202, "percap" 18689.9445908, "percent_college" 0.1905, "percent_black" 0.2609, "percent_married_children" 0.145}
   { "district_name" "CD 3, Pennsylvania", "geo_fips" 4203, "percap" 25533.7195473, "percent_college" 0.3494, "percent_black" 0.5711, "percent_married_children" 0.0798}
   { "district_name" "CD 4, Pennsylvania", "geo_fips" 4204, "percap" 41976.8517614, "percent_college" 0.4636, "percent_black" 0.0869, "percent_married_children" 0.24}
   { "district_name" "CD 5, Pennsylvania", "geo_fips" 4205, "percap" 33561.3035031, "percent_college" 0.3593, "percent_black" 0.2356, "percent_married_children" 0.1975}
   { "district_name" "CD 6, Pennsylvania", "geo_fips" 4206, "percap" 37818.5437747, "percent_college" 0.4272, "percent_black" 0.0563, "percent_married_children" 0.2432}
   { "district_name" "CD 7, Pennsylvania", "geo_fips" 4207, "percap" 29041.0201897, "percent_college" 0.2831, "percent_black" 0.054, "percent_married_children" 0.2039}
   { "district_name" "CD 8, Pennsylvania", "geo_fips" 4208, "percap" 24963.4558286, "percent_college" 0.2339, "percent_black" 0.0507, "percent_married_children" 0.1669}
   { "district_name" "CD 9, Pennsylvania", "geo_fips" 4209, "percap" 26334.3703736, "percent_college" 0.1999, "percent_black" 0.023, "percent_married_children" 0.1891}
   { "district_name" "CD 10, Pennsylvania", "geo_fips" 4210, "percap" 29923.4689981, "percent_college" 0.2974, "percent_black" 0.1024, "percent_married_children" 0.189}
   { "district_name" "CD 11, Pennsylvania", "geo_fips" 4211, "percap" 28031.3244511, "percent_college" 0.2456, "percent_black" 0.0318, "percent_married_children" 0.2235}
   { "district_name" "CD 12, Pennsylvania", "geo_fips" 4212, "percap" 24275.6896192, "percent_college" 0.2139, "percent_black" 0.0228, "percent_married_children" 0.181}
   { "district_name" "CD 13, Pennsylvania", "geo_fips" 4213, "percap" 25089.7085724, "percent_college" 0.1913, "percent_black" 0.0252, "percent_married_children" 0.1872}
   { "district_name" "CD 14, Pennsylvania", "geo_fips" 4214, "percap" 28392.8720931, "percent_college" 0.2487, "percent_black" 0.0302, "percent_married_children" 0.175}
   { "district_name" "CD 15, Pennsylvania", "geo_fips" 4215, "percap" 23668.332722, "percent_college" 0.1862, "percent_black" 0.0194, "percent_married_children" 0.1758}
   { "district_name" "CD 16, Pennsylvania", "geo_fips" 4216, "percap" 26376.716264, "percent_college" 0.2618, "percent_black" 0.0441, "percent_married_children" 0.1816}
   { "district_name" "CD 17, Pennsylvania", "geo_fips" 4217, "percap" 36053.01941, "percent_college" 0.3936, "percent_black" 0.0568, "percent_married_children" 0.1966}
   { "district_name" "CD 18, Pennsylvania", "geo_fips" 4218, "percap" 29340.6200601, "percent_college" 0.3568, "percent_black" 0.1822, "percent_married_children" 0.1371}])
