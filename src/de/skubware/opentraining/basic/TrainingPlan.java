package de.skubware.opentraining.basic;

import java.util.*;


/**
 * This class represents a TrainingPlan that consists of different
 * {@code Workout}s.
 * 
 * @author Christian Skubich
 * 
 */
public class TrainingPlan {
	ArrayList<Workout> workoutList = new ArrayList<Workout>();

	public TrainingPlan(List<Workout> wList) {
		this(wList.toArray(new Workout[wList.size()]));
	}

	/**
	 * Constructor
	 * 
	 * @throws NullPointerException
	 *             if any argument is null
	 */
	public TrainingPlan(Workout... workout) {
		// check null
		for (Workout w : workout) {
			if (w == null) {
				throw new NullPointerException();
			}
		}

		// assign values
		for (Workout w : workout) {
			this.workoutList.add(w);
		}
	}

	/**
	 * 
	 * @param fEx
	 * @return True if any workout contains the FitnessExercise
	 */
	public boolean contains(FitnessExercise fEx) {
		for (Workout w : workoutList) {
			for (FitnessExercise f : w.getFitnessExercises()) {
				if (f.equals(fEx)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Getter for workoutList
	 * 
	 * @return An unmodifiable list with all workouts of the TrainingPlan
	 */
	public List<Workout> getWorkoutList(){
		return workoutList;
	}

	public void addWorkout(Workout newWorkout) {
		this.workoutList.add(newWorkout);
	}

	/**
	 * Removes the given Workout
	 * 
	 * @param w
	 *            The Workout to remove *
	 * @throws NullPointerException
	 *             if the argument is null
	 * @throws IllegalArgumentException
	 *             if the Workout is not contained
	 * 
	 */
	public void removeWorkout(Workout w) {
		if (w == null) {
			throw new NullPointerException();
		}
		if (!this.workoutList.contains(w)) {
			throw new IllegalArgumentException();
		}

		this.workoutList.remove(w);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Size of workoutList: " + workoutList.size() + "\n\n");

		builder.append("Containing workouts: \n\n");
		for (Workout w : this.workoutList) {
			builder.append("\t" + w.toString());
			builder.append("\n\n");
		}

		builder.append("\n");

		return builder.toString();
	}

}