SELECT
  a.name AS artist_name,
  COUNT(aa.id) AS alias_count,
  STRING_AGG(aa.name, ', ' ORDER BY aa.name) AS aliases
FROM artist AS a
JOIN artist_alias AS aa
  ON a.id = aa.artist
WHERE
  a.name LIKE '%John' AND
  NOT EXISTS (
    SELECT
      1
    FROM artist_alias AS inner_aa
    WHERE
      inner_aa.artist = a.id ANDs
      LOWER(inner_aa.name) LIKE '%john%'
  )
GROUP BY
  a.id,
  a.name
HAVING
  COUNT(aa.id) > 0
ORDER BY
  a.name ASC;