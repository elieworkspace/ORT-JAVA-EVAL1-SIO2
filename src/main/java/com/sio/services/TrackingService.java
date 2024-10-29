package com.sio.services;

import com.sio.apis.MockChrevTzyonApiClient;
import com.sio.models.Position;
import com.sio.models.Target;
import com.sio.repositories.PositionRepository;
import com.sio.repositories.TargetRepository;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

public class TrackingService {

    private final MockChrevTzyonApiClient mockChrevTzyonApiClient = new MockChrevTzyonApiClient();
    private final TargetRepository targetRepository;
    private final PositionRepository positionRepository;

    public TrackingService() {
        this.targetRepository = new TargetRepository();
        this.positionRepository = new PositionRepository();
    }

    /**
     * Update the positions of all targets
     * If the target does not exist in the database, create it
     * If the target exists in the database, add its new position
     * Print the following message after the position is successfully acquired : - {target code name} : Position successfully acquired
     * Print the following message after the position is not acquired (API has not yet acquired the position : - {target code name} : Position not acquired
     */
    public void updateTargetsPositions() {
        //TODO implements this method
        MockChrevTzyonApiClient APIService = new MockChrevTzyonApiClient();
        APIService.getTargets();
        ArrayList<JSONObject> targets = APIService.getTargets();

        for (JSONObject onetarget : targets) {

            Target targetx = new Target();
            targetx.setHash((String) onetarget.get("hash"));
            targetx.setName((String) onetarget.get("name"));
            targetx.setCodeName((String) onetarget.get("code_name"));

            Target existingTarget = targetRepository.findByHash(targetx.getHash());


            // new position de la target avec un try catch auu cas ou il n y a pas de données
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
                    System.out.println(targetx.getName() + "Position successfuly added. ");
                } else {
                    System.out.println(targetx.getName() + "Position not aquired. ");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de l'ajout de la position : " + e);
            }
        }

    }


}
