package com.sio.services;

import com.sio.models.Target;
import com.sio.repositories.PositionRepository;
import com.sio.repositories.TargetRepository;

import java.util.ArrayList;

public class TargetService {

    private final TargetRepository tRepository;
    private final PositionRepository pRepository;

    public TargetService() {
        this.tRepository = new TargetRepository();
        this.pRepository = new PositionRepository();
    }

    /**
     * Get all targets stored in database and their respective positions
     * @return targets ArrayList
     *
     */
    public ArrayList<Target> getTargets() {
        //TODO implements this method

        // Retourn une arraylist pour chaqun avec  le nom, le code name, et le nombre de position.
        return tRepository.findAll();
    }

    /**
     * Add a target to the API
     * Print the following message after the target is succesfully added to the API : Target succesfully added, you will now have to wait 60 seconds before the target is available for position acquisition
     * @param codename String
     * @param name String
     */
    public void addTarget(String codename, String name) {
        //TODO implements this method
    }

    /**
     * Delete a target from the database and the API
     * @param t Target
     */
    public void deleteTarget(Target t) {
        //TODO implements this method

        // Supression via API
        MockChrevTzyonApiClient APIService = new MockChrevTzyonApiClient();
        APIService.deleteTarget(t);

        // Suppresion dans MySQL :
        TargetRepository targetRepository = new TargetRepository();
        targetRepository.delete(t);
    }

}
