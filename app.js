const stripe = require('stripe')('sk_test_51O7wJhCRoDqAtke7nS31cgEXAbCRYoNe6QNvBS8IFjxr9eGPLTyucA2ZPR68w3TP3fIaKvikZ4RYEfu6gPNjes0G00UyVmjmGo');
const express = require('express');
const jwt = require('jsonwebtoken');
const cors = require('cors');
const app = express();

const corsOptions = {
    origin: 'http://localhost:4200',
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
};

app.options('/create-checkout-session', cors(corsOptions));
app.use(cors());
app.use(express.json()); // Middleware pour analyser les données JSON du corps de la requête

app.post('/create-checkout-session', async (req, res) => {
    try {
        // Créez le JWT (JSON Web Token) avec les données reçues dans la requête
        const jwtPayload = req.body;
        const jwtToken = jwt.sign(jwtPayload, 'votre_clé_secrète_JWT');
        const encodedJwt = encodeURIComponent(jwtToken);
        const session = await stripe.checkout.sessions.create({
            submit_type: "pay",
            payment_method_types: ["card"],
            line_items: [
                {
                    price_data: {
                        currency: 'eur',
                        unit_amount: 500000,
                        product_data: {
                            name: `Location ${req.body.vehicule}`,
                            images: ['https://cdn.motor1.com/images/mgl/NGGZon/s3/koenigsegg-gemera.jpg']
                        }
                    },
                    quantity: 1
                }
            ],
            mode: 'payment',
            success_url: `http://localhost:4200/transaction-validee?transaction_id={CHECKOUT_SESSION_ID}&jwt=${encodedJwt}`,
            cancel_url: 'http://localhost:4200/cancel.html',
            payment_intent_data: {
                capture_method: 'manual',
                setup_future_usage: 'off_session',
                metadata: {
                    jwt: jwtToken
                }
            }
        });
        console.log({sessionId: session.id, sessionUrl: session.url})
        // Retournez la réponse JSON avec l'URL de la session
        res.status(200).json({ sessionId: session.id, sessionUrl: session.url });
    } catch (error) {
        // Gérez l'erreur ici et renvoyez une réponse JSON descriptive
        console.error('Erreur : ' + error.message); // Affichez l'erreur en direct
        res.status(500).json({ error: 'Erreur : ' + error.message });
    }
});

app.listen(4242, () => console.log('Running on port 4242'));
