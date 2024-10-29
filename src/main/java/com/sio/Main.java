package com.sio;

import com.sio.models.Target;
import com.sio.repositories.PositionRepository;
import com.sio.services.TargetService;
import com.sio.services.TrackingService;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final TargetService targetService = new TargetService();
    private static final TrackingService trackingService = new TrackingService();

    private static final PositionRepository positionRepository = new PositionRepository();

    private static final String GREEN = "\u001B[32m";
    private static String RESET = "\u001B[0m";

    public static void main(String[] args) {
        System.out.println(GREEN);
        printConnectBanner();

        while(true) {
            System.out.println("===============================================");
            System.out.println("Actions menu");
            System.out.println("===============================================");
            System.out.println("1. List targets");
            System.out.println("2. Acquire targets positions");
            System.out.println("3. Add target");
            System.out.println("4. Delete target");
            System.out.println("0. Exit");
            System.out.println("===============================================");

            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            ArrayList<Target> targets;
            switch (option) {
                case 1:
                    System.out.println("List targets");
                    System.out.println("-----------------------------------------------");
                    //TODO : Get all targets from the database and print them
                    System.out.println("-----------------------------------------------");
                    break;
                case 2:
                    System.out.println("Acquire target positions");
                    System.out.println("-----------------------------------------------");

                    trackingService.updateTargetsPositions();



                    System.out.println("-----------------------------------------------");
                    break;
                case 3:

                    System.out.println("Add target");
                    System.out.println("-----------------------------------------------");
                    //TODO : Add a target
                    System.out.println("input Codename:");
                    String codename = scanner.nextLine();
                    System.out.println("input Name:");
                    String name = scanner.nextLine();
                    targetService.addTarget(codename, name);
                    System.out.println("L'ajout dans l'API est effectué.");
                    System.out.println("Le script d'initialization ajoutera automatiquement cela dans MySQL. ");
                    System.out.println("-----------------------------------------------");
                    break;
                case 4:
                    System.out.println("Delete target");
                    System.out.println("-----------------------------------------------");
                    //TODO : Delete a target
                    System.out.println("-----------------------------------------------");

                    // Delete =
                    /*
                    *Prendre le hash
                    * Supprimer dans l'api
                    * Supprimer toutes les target avec ce hash dans les targets sql
                    * Supprimer toutes les positon avec ce hash
                    * */

                    ArrayList<Target> targetsy = targetService.getTargets();


                    for (int i = 0; i < targetsy.size(); i++) {
                        Target target = targetsy.get(i);
                        System.out.println((i + 1) + ". Code Name: " + target.getCodeName() +
                                ", Name: " + target.getName());
                    }

                    // demander de selectionner une target
                    System.out.print("Selectionner une target dans toutes cette liste: ");
                    int selection = scanner.nextInt();

                    // check si le select est valide
                    if (selection > 0 && selection <= targetsy.size()) {
                        Target selectedTarget = targetsy.get(selection - 1); // prendre la target selectionner
                        System.out.println("Votre choix:");
                        System.out.println("Code Name: " + selectedTarget.getCodeName() +
                                ", Name: " + selectedTarget.getName() +
                                ", Hash: " + selectedTarget.getHash() );

                        System.out.println(selectedTarget.getName() + " à " + positionRepository.countPositionsByTargetHash(selectedTarget.getHash()) + " positions.");



                        // Suppremer les positons :
                        positionRepository.deleteByTargetHash(selectedTarget.getHash());

                        // Supprimer la target :
                        targetService.deleteTarget(selectedTarget);

                    } else {
                        System.out.println("Invalid selection. Please try again.");
                    }

                    break;
                case 0:
                    printDisconnectBanner();
                    System.out.println(RESET);
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void printConnectBanner() {

        System.out.println("===============================================");
        System.out.println("    CHREV TZYON INTERFACE     ");
        System.out.println("===============================================");
        System.out.println("    Establishing connection to satellite...    ");
        System.out.println("===============================================");

        String[] progressIndicators = {"|", "/", "-", "\\"};
        int iterations = 10;

        for (int i = 0; i < iterations; i++) {
            System.out.print("\rConnecting " + progressIndicators[i % progressIndicators.length]);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        System.out.println("\rConnection established.        ");
        System.out.println("===============================================");
        System.out.println("Satellite link active. Ready to transmit data.");
        System.out.println("===============================================");

    }

    private static void printDisconnectBanner() {
        System.out.println("===============================================");
        System.out.println("    Closing connection to satellite...         ");
        System.out.println("===============================================");

        String[] progressIndicators = {"|", "/", "-", "\\"};
        int iterations = 10;

        for (int i = 0; i < iterations; i++) {
            System.out.print("\rDisconnecting " + progressIndicators[i % progressIndicators.length]);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        System.out.println("\rConnection Closed.        ");
        System.out.println("===============================================");

    }
}