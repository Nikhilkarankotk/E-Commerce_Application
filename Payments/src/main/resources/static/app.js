// Replace with your Stripe publishable key
const stripe = Stripe($('#stripe-public-key').val());
const elements = stripe.elements();
const card = elements.create('card');
card.mount('#card-element');
const form = document.getElementById('payment-form');
const submitButton = document.getElementById('submit');
const paymentStatus = document.getElementById('payment-status');
// Fetch Payment Intent and enable the submit button
fetch('http://localhost:9030/payments/create-intent', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }, // Ensure this is set
    body: JSON.stringify({
        orderId: 1, // Ensure this is a valid order ID
        amount: 1199.98 // Ensure this is a valid amount
    })
})
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch Payment Intent');
        }
        return response.text();
    })
    .then(data => {
        const clientSecret = data.clientSecret;
        console.log("Client Secret:", clientSecret); // Log the clientSecret
        submitButton.disabled = false;
    })
    .catch(error => {
        console.error("Error fetching PaymentIntent:", error);
        paymentStatus.textContent = "Error: " + error.message;
    });
