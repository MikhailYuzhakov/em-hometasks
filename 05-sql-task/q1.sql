SELECT
  r.name AS release_name,
  MIN(rc.date_year) AS release_year
FROM release AS r
JOIN artist_credit AS ac
  ON r.artist_credit = ac.id
JOIN artist_credit_name AS acn
  ON ac.id = acn.artist_credit
JOIN artist AS a
  ON acn.artist = a.id
JOIN medium AS m
  ON r.id = m.release
JOIN medium_format AS mf
  ON m.format = mf.id
JOIN release_info AS rc
  ON r.id = rc.release
JOIN area AS ar
  ON rc.area = ar.id
WHERE
  a.name = 'The Beatles' AND
  mf.name = '12" Vinyl' AND
  ar.name = 'United Kingdom' AND
  rc.date_year < 1970
GROUP BY
  r.name
ORDER BY
  release_year,
  release_name;
