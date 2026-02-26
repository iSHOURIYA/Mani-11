/* ─────────────────────────────────────────────────────────────────────────────
   Seat Booking System – app.js
   Vanilla JS / Fetch API – no framework dependencies
───────────────────────────────────────────────────────────────────────────── */

const API_BASE = 'http://localhost:8080/api';

// ── DOM references ─────────────────────────────────────────────────────────
const userSelect      = document.getElementById('user-select');
const dateInput       = document.getElementById('date-input');
const seatSelect      = document.getElementById('seat-select');
const seatHint        = document.getElementById('seat-hint');
const bookingForm     = document.getElementById('booking-form');
const bookingResult   = document.getElementById('booking-result');

const availDate       = document.getElementById('avail-date');
const checkAvailBtn   = document.getElementById('check-avail-btn');
const availResult     = document.getElementById('avail-result');

const cancelId        = document.getElementById('cancel-id');
const cancelBtn       = document.getElementById('cancel-btn');
const cancelResult    = document.getElementById('cancel-result');

// ── Helpers ────────────────────────────────────────────────────────────────

function showAlert(el, type, message) {
  el.className = `alert ${type}`;
  el.textContent = message;
  el.classList.remove('hidden');
}

function hideAlert(el) {
  el.classList.add('hidden');
  el.textContent = '';
}

function todayStr() {
  return new Date().toISOString().split('T')[0];
}

function maxDateStr() {
  const d = new Date();
  d.setDate(d.getDate() + 14);
  return d.toISOString().split('T')[0];
}

// ── Populate date limits ───────────────────────────────────────────────────
(function setDateLimits() {
  const today = todayStr();
  const max   = maxDateStr();
  dateInput.min  = today;
  dateInput.max  = max;
  dateInput.value = today;

  availDate.min  = today;
  availDate.max  = max;
  availDate.value = today;
})();

// ── Load users ─────────────────────────────────────────────────────────────
async function loadUsers() {
  try {
    const res   = await fetch(`${API_BASE}/users`);
    const users = await res.json();
    userSelect.innerHTML = '<option value="">— choose employee —</option>';
    users.forEach(u => {
      const opt = document.createElement('option');
      opt.value       = u.id;
      opt.textContent = `${u.name} (${u.batch.replace('_', ' ')})`;
      userSelect.appendChild(opt);
    });
  } catch (err) {
    showAlert(bookingResult, 'error', 'Could not load users. Is the backend running?');
  }
}

// ── Load available seats for a given date ─────────────────────────────────
async function loadSeatsForDate(dateStr) {
  seatSelect.innerHTML = '<option value="">Loading…</option>';
  seatHint.textContent  = '';

  try {
    const res      = await fetch(`${API_BASE}/availability?date=${dateStr}`);
    const seats    = await res.json();
    const available = seats.filter(s => s.available);

    seatSelect.innerHTML = '<option value="">— choose seat —</option>';

    if (available.length === 0) {
      seatHint.textContent = 'No seats available for this date.';
      return;
    }

    available.forEach(s => {
      const opt = document.createElement('option');
      opt.value       = s.seatId;
      opt.textContent = `Seat ${s.seatNumber} (${s.seatType})`;
      seatSelect.appendChild(opt);
    });

    seatHint.textContent = `${available.length} seat(s) available.`;
  } catch (err) {
    seatSelect.innerHTML = '<option value="">Error loading seats</option>';
    seatHint.textContent  = 'Backend unreachable.';
  }
}

// ── Date change → refresh available seats ─────────────────────────────────
dateInput.addEventListener('change', () => {
  if (dateInput.value) {
    loadSeatsForDate(dateInput.value);
    hideAlert(bookingResult);
  }
});

// ── Booking form submit ────────────────────────────────────────────────────
bookingForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  hideAlert(bookingResult);

  const userId      = userSelect.value;
  const seatId      = seatSelect.value;
  const bookingDate = dateInput.value;

  if (!userId || !seatId || !bookingDate) {
    showAlert(bookingResult, 'error', 'Please fill in all fields.');
    return;
  }

  const bookBtn = document.getElementById('book-btn');
  bookBtn.disabled = true;
  bookBtn.textContent = 'Booking…';

  try {
    const res  = await fetch(`${API_BASE}/book`, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify({
        userId:      parseInt(userId),
        seatId:      parseInt(seatId),
        bookingDate: bookingDate
      })
    });
    const data = await res.json();

    if (res.ok) {
      showAlert(bookingResult, 'success',
        `✅ Booking #${data.bookingId} confirmed! Seat ${data.seatNumber} (${data.seatType}) on ${data.bookingDate}.`);
      // Refresh seat list
      loadSeatsForDate(bookingDate);
      seatSelect.value = '';
    } else {
      showAlert(bookingResult, 'error', `❌ ${data.message || 'Booking failed.'}`);
    }
  } catch (err) {
    showAlert(bookingResult, 'error', '❌ Network error. Is the backend running?');
  } finally {
    bookBtn.disabled = false;
    bookBtn.textContent = 'Book Seat';
  }
});

// ── Availability checker ───────────────────────────────────────────────────
checkAvailBtn.addEventListener('click', async () => {
  const dateStr = availDate.value;
  if (!dateStr) {
    availResult.innerHTML = '<p class="muted">Please select a date first.</p>';
    return;
  }

  availResult.innerHTML = '<p class="muted">Loading…</p>';

  try {
    const res   = await fetch(`${API_BASE}/availability?date=${dateStr}`);
    const seats = await res.json();

    const available = seats.filter(s => s.available).length;
    const total     = seats.length;

    let html = `<p class="avail-summary">${available} of ${total} seats available on <strong>${dateStr}</strong></p>`;
    html += '<div class="seat-grid">';

    seats.forEach(s => {
      const cls = s.available ? 'available' : 'booked';
      html += `<div class="seat-cell ${cls}">
        ${s.seatNumber}
        <span class="seat-type">${s.seatType}</span>
      </div>`;
    });

    html += '</div>';
    availResult.innerHTML = html;
  } catch (err) {
    availResult.innerHTML = '<p class="muted" style="color:var(--error-cl)">Error loading availability.</p>';
  }
});

// ── Cancel booking ─────────────────────────────────────────────────────────
cancelBtn.addEventListener('click', async () => {
  hideAlert(cancelResult);
  const id = cancelId.value.trim();

  if (!id || isNaN(parseInt(id))) {
    showAlert(cancelResult, 'error', 'Please enter a valid Booking ID.');
    return;
  }

  cancelBtn.disabled = true;
  cancelBtn.textContent = 'Cancelling…';

  try {
    const res  = await fetch(`${API_BASE}/cancel/${parseInt(id)}`, { method: 'DELETE' });
    const data = await res.json();

    if (res.ok) {
      showAlert(cancelResult, 'success',
        `✅ Booking #${data.bookingId} cancelled. Seat ${data.seatNumber} is now free.`);
      cancelId.value = '';
    } else {
      showAlert(cancelResult, 'error', `❌ ${data.message || 'Cancellation failed.'}`);
    }
  } catch (err) {
    showAlert(cancelResult, 'error', '❌ Network error. Is the backend running?');
  } finally {
    cancelBtn.disabled = false;
    cancelBtn.textContent = 'Cancel';
  }
});

// ── Register Service Worker ────────────────────────────────────────────────
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('./service-worker.js')
      .then(reg => console.log('SW registered:', reg.scope))
      .catch(err => console.warn('SW registration failed:', err));
  });
}

// ── Kick-off ───────────────────────────────────────────────────────────────
loadUsers();
loadSeatsForDate(dateInput.value);
