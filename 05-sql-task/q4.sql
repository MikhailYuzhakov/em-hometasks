SELECT
  (FLOOR(a.begin_date_year / 10) * 10 || 's') AS decade,
  COUNT(a.id) AS group_count
FROM artist AS a
JOIN artist_type AS at
  ON a.type = at.id
WHERE
  at.name = 'Group' AND
  a.area = 222 AND -- ID для United States
  a.begin_date_year IS NOT NULL AND
  a.begin_date_year >= 1900 AND
  a.begin_date_year <= 2023
GROUP BY
  decade
ORDER BY
  decade;
