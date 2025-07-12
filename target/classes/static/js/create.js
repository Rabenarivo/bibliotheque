document.addEventListener("DOMContentLoaded", () => {
    const validateButtons = document.querySelectorAll("[id^='validate-button-']");
    validateButtons.forEach(button => {
        if (button) {
            button.addEventListener("click", (event) => {
                // Add your validation logic here
                console.log("Validate button clicked:", event.target.id);
            });
        }
    });

    const deleteButtons = document.querySelectorAll("[id^='delete-button-']");
    deleteButtons.forEach(button => {
        if (button) {
            button.addEventListener("click", (event) => {
                // Add your delete logic here
                console.log("Delete button clicked:", event.target.id);
            });
        }
    });
});
