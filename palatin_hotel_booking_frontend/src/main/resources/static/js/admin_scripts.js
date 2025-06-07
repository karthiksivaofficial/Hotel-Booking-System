console.log('Loaded admin_scripts.js version 2025-04-25-v2');

document.getElementById('generate-room-fields').addEventListener('click', function() {
    const numberOfRoomsInput = document.getElementById('numberOfRooms');
    const numberOfRooms = parseInt(numberOfRoomsInput.value);
    const roomFields = document.getElementById('room-fields');
    roomFields.innerHTML = '';

    if (isNaN(numberOfRooms) || numberOfRooms < 1 || numberOfRooms > 2) {
        alert('Please enter a valid number of rooms (1 or 2).');
        return;
    }

    console.log(`Generating fields for ${numberOfRooms} rooms`);

    for (let i = 0; i < numberOfRooms; i++) {
        const roomDiv = document.createElement('div');
        roomDiv.className = 'room-field mb-3';
        roomDiv.innerHTML = `
            <h5>Room ${i + 1}</h5>
            <div class="mb-2">
                <label class="form-label">Room Number (e.g., 1F-101)</label>
                <input type="text" class="form-control" name="rooms[${i}].roomNumber" required>
                <div class="invalid-feedback">Please enter a valid room number.</div>
            </div>
            <div class="mb-2">
                <label class="form-label">Type</label>
                <select class="form-control" name="rooms[${i}].type" required>
                    <option value="">Select type</option>
                    <option value="SINGLE">Single</option>
                    <option value="DOUBLE">Double</option>
                </select>
                <div class="invalid-feedback">Please select a room type.</div>
            </div>
            <div class="mb-2">
                <label class="form-label">View</label>
                <select class="form-control" name="rooms[${i}].view" required>
                    <option value="">Select view</option>
                    <option value="TOP_VIEW">Top View</option>
                    <option value="SEASIDE">Seaside</option>
                </select>
                <div class="invalid-feedback">Please select a view.</div>
            </div>
            <div class="mb-2">
                <label class="form-label">Price</label>
                <input type="number" class="form-control" name="rooms[${i}].price" min="0" step="0.01" required>
                <div class="invalid-feedback">Please enter a valid price (0 or higher).</div>
            </div>
            <div class="mb-2">
                <label class="form-label">Amenities (comma-separated)</label>
                <input type="text" class="form-control" name="rooms[${i}].amenities">
            </div>
            <div class="mb-2">
                <label class="form-label">Floor</label>
                <input type="number" class="form-control" name="rooms[${i}].floor" value="${document.getElementById('floorNumber').value}" readonly>
            </div>
            <div class="mb-2">
                <label class="form-label">Status</label>
                <select class="form-control" name="rooms[${i}].status">
                    <option value="AVAILABLE" selected>Available</option>
                    <option value="OCCUPIED">Occupied</option>
                    <option value="OUT_OF_SERVICE">Out of Service</option>
                </select>
            </div>
        `;
        roomFields.appendChild(roomDiv);
        console.log(`Generated fields for room ${i}: rooms[${i}].roomNumber, rooms[${i}].type, rooms[${i}].view, rooms[${i}].price, rooms[${i}].amenities, rooms[${i}].floor, rooms[${i}].status`);
    }

    document.getElementById('submit-rooms').disabled = false;
});

document.getElementById('floor-form').addEventListener('submit', function(event) {
    const form = this;
    if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
        form.classList.add('was-validated');
        console.log('Form validation failed');
    } else {
        form.classList.add('was-validated');
        console.log('Form validation passed, submitting form');
    }
});