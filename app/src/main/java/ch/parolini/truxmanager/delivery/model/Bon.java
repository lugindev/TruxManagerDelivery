package ch.parolini.truxmanager.delivery.model;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Toni on 02.10.2016.
 */
public class Bon  {
    private String nom ;
    private String numero;

    private String plaque;

    public Bon() {
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPlaque() {
        return plaque;
    }

    public void setPlaque(String plaque) {
        this.plaque = plaque;
    }
}
