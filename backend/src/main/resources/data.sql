-- ─────────────────────────────────────────────────────────────────────────────
-- SEED DATA  –  runs once on startup (idempotent because of ON CONFLICT DO NOTHING)
-- ─────────────────────────────────────────────────────────────────────────────

-- ── 40 FIXED seats (1–40) ───────────────────────────────────────────────────
INSERT INTO seats (seat_number, type)
SELECT gs, 'FIXED'
FROM generate_series(1, 40) AS gs
ON CONFLICT (seat_number) DO NOTHING;

-- ── 10 FLOATER seats (41–50) ────────────────────────────────────────────────
INSERT INTO seats (seat_number, type)
SELECT gs, 'FLOATER'
FROM generate_series(41, 50) AS gs
ON CONFLICT (seat_number) DO NOTHING;

-- ── Sample users ─────────────────────────────────────────────────────────────
INSERT INTO users (name, batch) VALUES
    ('Alice Johnson',   'BATCH_1'),
    ('Bob Smith',       'BATCH_1'),
    ('Charlie Brown',   'BATCH_1'),
    ('Diana Prince',    'BATCH_1'),
    ('Ethan Hunt',      'BATCH_1'),
    ('Fiona Green',     'BATCH_2'),
    ('George White',    'BATCH_2'),
    ('Hannah Black',    'BATCH_2'),
    ('Ivan Torres',     'BATCH_2'),
    ('Julia Roberts',   'BATCH_2')
ON CONFLICT DO NOTHING;
