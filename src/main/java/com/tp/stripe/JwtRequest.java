package com.tp.stripe;

import lombok.Data;

@Data
public class JwtRequest {
    private String nom;
    private String prenom;
    private int age;
    private String ville;
    private String vehicule;
    private String stripeToken;


}
