SELECT
  r.name AS release_name,
  ac.name AS artist_name,
  ri.date_year AS release_year
FROM release AS r
JOIN artist_credit AS ac
  ON r.artist_credit = ac.id
JOIN medium AS m
  ON r.id = m.release
JOIN medium_format AS mf
  ON m.format = mf.id
JOIN release_info AS ri
  ON r.id = ri.release
WHERE
  mf.name = 'Cassette' AND
  ri.date_year IS NOT NULL
ORDER BY
  release_year DESC,
  COALESCE(ri.date_month, 0) DESC,
  COALESCE(ri.date_day, 0) DESC,
  release_name ASC,
  artist_name ASC
LIMIT 10;