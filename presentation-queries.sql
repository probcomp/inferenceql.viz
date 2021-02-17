SELECT gender, age, bmi, smoker, health_status, exercise FROM data;

----------------------------------anomaly detection

SELECT (PROBABILITY DENSITY OF bmi UNDER model AS prob),
       bmi, exercise, gender, health_status, smoker
FROM data;

SELECT (PROBABILITY DENSITY OF bmi
        GIVEN exercise
        UNDER model AS prob),
       bmi, exercise, gender, health_status, smoker
FROM data;

SELECT (PROBABILITY DENSITY OF bmi
        GIVEN exercise, gender, health_status, smoker
        UNDER model AS prob),
       bmi, exercise, gender, health_status, smoker
FROM data;

SELECT (PROBABILITY DENSITY OF bmi
        GIVEN age
        UNDER model AS prob),
       bmi, age, exercise, gender, health_status, smoker
FROM data;

----------------------------------generating data

SELECT age, bmi, exercise, gender, health_status, smoker 
FROM (GENERATE age, bmi, exercise, gender, health_status, smoker
      UNDER model)
LIMIT 100


SELECT age, bmi, gender, health_status, smoker
FROM (GENERATE age, bmi, gender, health_status, smoker
      GIVEN exercise="0.0"
      UNDER model)
LIMIT 100;

----------------------------------hypothetical rows -- looking at simulation plots

WITH (INSERT INTO data VALUES (editable=true, bmi=50, smoker="current")) AS data,
     (INSERT INTO data VALUES (editable=true, bmi=20, smoker="never")) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise FROM data;

----------------------------------hypothetical rows -- incorporating values

WITH (INSERT INTO data VALUES (editable=true, bmi=70)) AS data,
     (INSERT INTO data VALUES (editable=true, bmi=80)) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise, 
       (PROBABILITY DENSITY OF bmi
        UNDER model
        AS prob)
FROM data;

WITH (INSERT INTO data VALUES (editable=true, bmi=70)) AS data,
     (INSERT INTO data VALUES (editable=true, bmi=80)) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise, 
       (PROBABILITY DENSITY OF bmi
        UNDER (INCORPORATE ROW (bmi=70) INTO (INCORPORATE ROW (bmi=80) INTO model))
        AS prob)
FROM data;

-----------------------labeling queries

WITH (ALTER data ADD label) AS data,
     (UPDATE data SET label=true WHERE rowid=721 OR rowid=1302 OR rowid=502 OR rowid=1947 OR rowid=510) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise,
       (PROBABILITY OF label=true
        GIVEN bmi, health_status
        UNDER (INCORPORATE COLUMN (502=true, 510=true, 721=true, 1302=true, 1947=true) AS label INTO model)
        AS prob)
FROM data;

WITH (ALTER data ADD label) AS data,
     (UPDATE data SET label=true WHERE rowid=721 OR rowid=1302 OR rowid=502 OR rowid=1947 OR rowid=510) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise,
       (PROBABILITY OF label=true
        GIVEN bmi, health_status, gender, exercise
        UNDER (INCORPORATE COLUMN (502=true, 510=true, 721=true, 1302=true, 1947=true) AS label INTO model)
        AS prob)
FROM data;
