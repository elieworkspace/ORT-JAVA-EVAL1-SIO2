package com.sio.initialization;

import com.sio.apis.MockChrevTzyonApiClient;
import com.sio.models.Position;
import com.sio.models.Target;
import com.sio.repositories.PositionRepository;
import com.sio.repositories.TargetRepository;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

public class initialization {

    public static void main(String[] args) {

        MockChrevTzyonApiClient APIService = new MockChrevTzyonApiClient();
        TargetRepository targetRepository = new TargetRepository();
        PositionRepository positionRepository = new PositionRepository();

        // recuperer les targets
        APIService.getTargets();

        // exécuter l'appel de la logique toutes les 1 minute
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // L'api se met a joourrs toutes les minutes
                        // Donc je recupeere les données de l'api
                        ArrayList<JSONObject> targets = APIService.getTargets();

                        for (JSONObject onetarget : targets) {

                            Target targetx = new Target();
                            targetx.setHash((String) onetarget.get("hash"));
                            targetx.setName((String) onetarget.get("name"));
                            targetx.setCodeName((String) onetarget.get("code_name"));

                            Target existingTarget = targetRepository.findByHash(targetx.getHash());

                            if (existingTarget == null) {
                                targetRepository.create(targetx);
                                existingTarget = targetx;
                                System.out.println("Nouvelle target : " + targetx.getName());
                            } else {
                                System.out.println("Target deja existente : " + targetx.getName());
                            }

                            // ajout des new coordonnées - on ne parle pas ici de maj mais d'ajout dans la db
                            try {
                                Float latitude = ((Number) onetarget.get("latitude")).floatValue();
                                Float longitude = ((Number) onetarget.get("longitude")).floatValue();
                                String timestampString = (String) ((JSONObject) onetarget.get("updated_at")).get("Time");
                                Instant instant = Instant.parse(timestampString);
                                Timestamp timestamp = Timestamp.from(instant);

                                ArrayList<Position> existingPositions = positionRepository.findByTargetHash(existingTarget.getHash());
                                boolean isDuplicate = existingPositions.stream().anyMatch(pos ->
                                        pos.getLatitude().equals(latitude) && pos.getLongitude().equals(longitude)
                                );
                                if (!isDuplicate) {
                                    Position newPosition = new Position(existingTarget, latitude, longitude, timestamp);
                                    positionRepository.create(newPosition);
                                    System.out.println("New position pour la target " + targetx.getName() + " : Lat=" + latitude + ", Lon=" + longitude);
                                } else {
                                    System.out.println("Position deja exstente pour " + targetx.getName());
                                }
                            } catch (Exception e) {
                                System.out.println("Erreur pendant l'ajout  : " + e);
                            }
                        }


                    }
                },
                0,      // 0 temps avant la premiere executuon
                60000   // intercalle entre chaque execution - millisecondes :  60 000 ms = 1 minute
        );
    }
}
