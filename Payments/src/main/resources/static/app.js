async function main() {
    const res = await fetch('/api/stripe/public-key');
    const stripe = Stripe(await res.text());

    // Or use your test key directly (not recommended for production)
    // const stripe = Stripe('pk_test_51REBtgFa1eW0LGEiEVhZLcWdfNSzhG9ZEp4MWeRswBMFx4N6QAVRwjm83bg4bnaLwPHRWT9e1LV6sLmBIHrVTkU600XMg8cgCT');

    const elements = stripe.elements();
    const card = elements.create('card');
    card.mount('#card-element');

    const form = document.getElementById('payment-form');
    const submitButton = document.getElementById('submit');
    const paymentStatus = document.getElementById('payment-status');

    fetch('/payments/create-intent', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            orderId: 1, // Replace with the actual order ID
            // amount: 1199.98 // Replace with the actual amount
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch Payment Intent');
            }
            return response.json();
        })
        .then(data => {
            const clientSecret = data.clientSecret;
            console.log("Client Secret:", clientSecret); // Log the clientSecret
            submitButton.disabled = false;
            // Handle form submission
            form.addEventListener('submit', async (event) => {
                event.preventDefault();
                const { error, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
                    payment_method: {
                        card: card,
                        billing_details: {
                            name: 'John Doe' // Replace with actual name
                        }
                    }
                });
                if (error) {
                    paymentStatus.textContent = "Payment failed: " + error.message;
                } else if (paymentIntent.status === 'succeeded') {
                    paymentStatus.textContent = "Payment successful!";
                    // Notify the backend of the successful payment
                    fetch('/payments/confirm', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ paymentIntentId: paymentIntent.id })
                    }).then(response => response.json())
                        .then(data => {
                            console.log("Payment confirmed:", data);
                            paymentStatus.textContent = "Payment confirmed: " + JSON.stringify(data);
                        });
                }
            });
        })
        .catch(error => {
            console.error("Error fetching PaymentIntent:", error);
            paymentStatus.textContent = "Error: " + error.message;
        });
}

main();
