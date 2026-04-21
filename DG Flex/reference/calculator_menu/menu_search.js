document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('calculator-search');
    const calculatorList = document.getElementById('calculator-list');
    const cards = calculatorList.querySelectorAll('.menu-card');

    searchInput.addEventListener('input', () => {
        const searchTerm = searchInput.value.toLowerCase().trim();

        cards.forEach(card => {
            const title = card.querySelector('h3').textContent.toLowerCase();
            const description = card.querySelector('p').textContent.toLowerCase();
            const cardText = title + ' ' + description;

            if (cardText.includes(searchTerm)) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    });
});