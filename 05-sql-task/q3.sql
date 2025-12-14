SELECT
  a.name AS artist_name,
  MIN(r_count.release_month) AS month_with_most_releases,
  MAX(r_count.release_count) AS max_releases
FROM artist AS a
JOIN artist_type AS at
  ON a.type = at.id
JOIN (
  SELECT
    acn.artist,
    ri.date_month AS release_month,
    COUNT(r.id) AS release_count,
    ROW_NUMBER() OVER (PARTITION BY acn.artist ORDER BY COUNT(r.id) DESC, ri.date_month ASC) AS rn
  FROM release AS r
  JOIN artist_credit AS ac
    ON r.artist_credit = ac.id
  JOIN artist_credit_name AS acn
    ON ac.id = acn.artist_credit
  JOIN release_info AS ri
    ON r.id = ri.release
  WHERE
    ri.date_month IS NOT NULL
  GROUP BY
    acn.artist,
    ri.date_month
) AS r_count
  ON a.id = r_count.artist
WHERE
  at.name = 'Person' AND
  a.name LIKE 'Elvis%' AND
  r_count.rn = 1
GROUP BY
  a.name
ORDER BY
  max_releases DESC,
  artist_name ASC;
