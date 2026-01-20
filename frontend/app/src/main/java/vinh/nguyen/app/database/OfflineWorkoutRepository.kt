/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vinh.nguyen.app.database

import kotlinx.coroutines.flow.Flow

class OfflineWorkoutRepository(private val workoutEntryDao: WorkoutEntryDao) : WorkoutRepository {
    override fun getAllWorkoutsStream(): Flow<List<Workout>> = workoutEntryDao.getAllWorkoutsStream()

    override fun getWorkoutStream(id: Int): Flow<Workout?> = workoutEntryDao.getWorkoutStream(id)

    override suspend fun getTotalReps(): Int = workoutEntryDao.getTotalReps()

    override suspend fun insertWorkout(workout: Workout) = workoutEntryDao.insertWorkout(workout)

    override suspend fun deleteWorkout(workout: Workout) = workoutEntryDao.deleteWorkout(workout)

    override suspend fun updateWorkout(workout: Workout) = workoutEntryDao.updateWorkout(workout)
}