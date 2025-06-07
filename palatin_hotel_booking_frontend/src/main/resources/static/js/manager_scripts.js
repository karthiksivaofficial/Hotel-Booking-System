document.addEventListener('DOMContentLoaded', function () {
    const generateBtn = document.getElementById('generate-room-fields');
    const roomFieldsDiv = document.getElementById('room-fields');
    const submitBtn = document.getElementById('submit-rooms');
    const numberOfRoomsInput = document.getElementById('numberOfRooms');
    const floorNumberInput = document.getElementById('floorNumber');

    function generateRoomFields(numberOfRooms, floors = 1, baseFloor = 1) {
        roomFieldsDiv.innerHTML = '';
        const roomsPerFloor = numberOfRooms;
        let roomIndex = 1;

        const floor = baseFloor;
        const floorDiv = document.createElement('div');
        floorDiv.innerHTML = `<h4>Floor ${floor}</h4>`;
        for (let i = 0; i < roomsPerFloor && roomIndex <= numberOfRooms; i++, roomIndex++) {
            const roomDiv = document.createElement('div');
            roomDiv.className = 'mb-3 border p-3';
            roomDiv.innerHTML = `
                <h5>Room ${floor}F-${100 + i + 1}</h5>
                <input type="hidden" name="rooms[${roomIndex - 1}].roomNumber" value="${floor}F-${100 + i + 1}">
                <div class="mb-2">
                    <label class="form-label">Type</label>
                    <select class="form-control" name="rooms[${roomIndex - 1}].type" required>
                        <option value="SINGLE">Single</option>
                        <option value="DOUBLE">Double</option>
                    </select>
                </div>
                <div class="mb-2">
                    <label class="form-label">View</label>
                    <select class="form-control" name="rooms[${roomIndex - 1}].view" required>
                        <option value="TOP_VIEW">Top View</option>
                        <option value="SEASIDE">Seaside</option>
                    </select>
                </div>
                <div class="mb-2">
                    <label class="form-label">Price</label>
                    <input type="number" class="form-control" name="rooms[${roomIndex - 1}].price" min="0" required>
                </div>
                <div class="mb-2">
                    <label class="form-label">Amenities (comma-separated)</label>
                    <input type="text" class="form-control" name="rooms[${roomIndex - 1}].amenities">
                </div>
                <input type="hidden" name="rooms[${roomIndex - 1}].floor" value="${floor}">
                <input type="hidden" name="rooms[${roomIndex - 1}].status" value="AVAILABLE">
            `;
            floorDiv.appendChild(roomDiv);
        }
        roomFieldsDiv.appendChild(floorDiv);
        submitBtn.disabled = false;
    }

    if (generateBtn) {
        generateBtn.addEventListener('click', () => {
            const numberOfRooms = parseInt(numberOfRoomsInput.value) || 0;
            const floorNumber = parseInt(floorNumberInput.value) || 1;
            if (numberOfRooms > 0) {
                generateRoomFields(numberOfRooms, 1, floorNumber);
            } else {
                roomFieldsDiv.innerHTML = '<p class="text-danger">Please enter a valid number of rooms.</p>';
                submitBtn.disabled = true;
            }
        });
    }
});