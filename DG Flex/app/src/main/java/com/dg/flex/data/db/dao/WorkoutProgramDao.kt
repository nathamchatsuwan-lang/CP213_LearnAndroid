package com.dg.flex.data.db.dao

import com.dg.flex.data.db.entity.ProgramExercise
import com.dg.flex.data.db.entity.RemovePlan
import com.dg.flex.data.db.entity.WorkoutProgram
import com.dg.flex.data.db.entity.WorkoutProgramRename
import com.dg.flex.data.db.entity.WorkoutProgramReorder
import com.dg.flex.data.db.entity.WorkoutProgramUpdateDays
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutProgramDao {

    @Query(
        "SELECT * FROM `program` " +
        "LEFT JOIN programexercise ON `program`.programId = programexercise.extProgramId " +
        "WHERE `program`.extPlanId LIKE :planId"
    )
    fun getProgramsMapExercises(planId: Long): Flow<Map<WorkoutProgram, List<ProgramExercise>>>

    @Query(
        "SELECT * FROM `program` " +
        "LEFT JOIN programexercise ON `program`.programId = programexercise.extProgramId " +
        "WHERE `program`.programId LIKE :programId"
    )
    fun getProgramMapExercises(programId: Long): Flow<Map<WorkoutProgram, List<ProgramExercise>>>

    @Query("SELECT * FROM `program` WHERE `program`.extPlanId LIKE :planId")
    fun getPrograms(planId: Long): Flow<List<WorkoutProgram>>


    @Query("SELECT * FROM `program` WHERE `program`.programId LIKE :programId")
    fun getProgram(programId: Long): Flow<WorkoutProgram>

    @Insert
    suspend fun insert(program: WorkoutProgram): Long

    @Update(entity = WorkoutProgram::class)
    suspend fun updateName(workoutProgramRename: WorkoutProgramRename)

    @Update(entity = WorkoutProgram::class)
    suspend fun updateOrder(workoutProgramReorders: List<WorkoutProgramReorder>)

    @Update(entity = WorkoutProgram::class)
    suspend fun updateDays(workoutProgramUpdateDays: WorkoutProgramUpdateDays)

    @Update(entity = WorkoutProgram::class)
    suspend fun removeFromPlan(removePlan: RemovePlan)

}
