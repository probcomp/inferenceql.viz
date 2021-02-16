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

----------------------------------hypotheical rows

WITH (ALTER data ADD label) AS data,
     (UPDATE data SET label=true WHERE rowid=195 OR rowid=268) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise, 
       (PROBABILITY OF label=true 
        GIVEN gender, age, bmi, smoker, health_status, exercise
        UNDER model
        AS prob)
FROM data;


WITH (INSERT INTO data VALUES (editable=true, bmi=70)) AS data,
     (INSERT INTO data VALUES (editable=true, bmi=80)) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise, 
       (PROBABILITY OF bmi 
        UNDER model
        AS prob)
FROM data;

WITH (INSERT INTO data VALUES (editable=true, bmi=70)) AS data,
     (INSERT INTO data VALUES (editable=true, bmi=80)) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise, 
       (PROBABILITY OF bmi 
        UNDER (INCORPORATE ROW (bmi=70) INTO (INCORPORATE ROW (bmi=80) INTO model))
        AS prob)
FROM data;


----------------------------------labeling

WITH (ALTER data ADD label) AS data,
     (UPDATE data SET label=true WHERE rowid=1337 OR rowid=1979) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise,
       (PROBABILITY OF label=true 
        GIVEN bmi, health_status
        UNDER model
        AS prob)
FROM data;

WITH (ALTER data ADD label) AS data,
     (UPDATE data SET label=true WHERE rowid=1337 OR rowid=1979) AS data:
SELECT gender, age, bmi, smoker, health_status, exercise,
       (PROBABILITY OF label=true 
        GIVEN bmi, health_status
        UNDER (INCORPORATE COLUMN (1337=true, 1979=true) AS label INTO model)
        AS prob)
FROM data;
