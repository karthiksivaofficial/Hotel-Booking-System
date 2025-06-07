document.addEventListener('DOMContentLoaded', function () {
    const checkboxes = document.querySelectorAll('.room-checkbox');
    const bookBtn = document.getElementById('book-btn');
    const selectedRoomsInput = document.getElementById('selected-rooms');

    function updateBookButton() {
        const selected = Array.from(checkboxes).filter(cb => cb.checked).map(cb => cb.value);
        selectedRoomsInput.value = selected.join(',');
        bookBtn.disabled = selected.length === 0;
    }

    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateBookButton);
    });

    updateBookButton();
});