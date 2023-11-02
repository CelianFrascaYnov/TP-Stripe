package com.tp.stripe;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class JwtController {

    @Value("${stripe.secretKey}")
    private String stripeSecretKey;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody JwtRequest jwtRequest) {
        try {
            // Créez le JWT
            ObjectMapper objectMapper = new ObjectMapper();
            String payloadJson = objectMapper.writeValueAsString(jwtRequest);
            String jwtToken = Jwts.builder()
                    .setPayload(payloadJson)
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();

            // Configurez votre clé d'API Stripe
            Stripe.apiKey = stripeSecretKey;
            System.out.println(stripeSecretKey);
            // Créez la session de paiement Stripe
            String YOUR_DOMAIN = "http://localhost:4242";
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(YOUR_DOMAIN + "?success=true")
                    .setCancelUrl(YOUR_DOMAIN + "?canceled=true")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPrice("price_1O812HCRoDqAtke7Pv0sgrlW")
                                    .build())
                    .build();

            Session session = Session.create(params);
            System.out.println(session);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            // Gérer l'erreur ici, par exemple, en journalisant l'erreur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
        }
    }
}
