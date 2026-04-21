package com.dg.flex.ui.screens.plans

import com.dg.flex.data.PreferenceRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dg.flex.data.db.entity.WorkoutPlan
import com.dg.flex.data.Repository
import com.dg.flex.data.db.entity.WorkoutPlanDifficulty
import com.dg.flex.data.db.entity.WorkoutPlanGoal
import com.dg.flex.data.db.entity.WorkoutPlanSplit
import com.dg.flex.data.db.entity.WorkoutProgram
import com.dg.flex.utils.generatePlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeneratePlanState(
    val generatedPlan: WorkoutPlan? = null,
    val workoutPlanMapPrograms: List<Pair<WorkoutPlan, List<WorkoutProgram>>> = emptyList(),
    val openAddPlanDialogue: Boolean = false,
    val currentPlanId: Long? = null
)

sealed class GeneratePlanEvent{
    data class GeneratePlan(
        val goalChoice: WorkoutPlanGoal,
        val expertiseLevel: WorkoutPlanDifficulty,
        val workoutSplit: WorkoutPlanSplit
    ): GeneratePlanEvent()

}

@HiltViewModel
class GeneratePlanViewModel @Inject constructor(
    private val repository: Repository,
    private val preferences: PreferenceRepository
): ViewModel() {
    private val _state = MutableStateFlow(GeneratePlanState())
    val state: StateFlow<GeneratePlanState> = _state.asStateFlow()

    private var generatePlanJob: Job? = null

    private fun updatePlans(
        currentPlanId: Long? = state.value.currentPlanId,
        workoutPlanMapPrograms: List<Pair<WorkoutPlan, List<WorkoutProgram>>> = state.value.workoutPlanMapPrograms
    ){
        var plans = workoutPlanMapPrograms
        if(currentPlanId != null){
            plans = workoutPlanMapPrograms.sortedByDescending {plan ->
                if (plan.first.planId == currentPlanId) 1 else 0
            }
        }
        _state.update { it.copy(
            workoutPlanMapPrograms = plans,
            currentPlanId = currentPlanId
        )}
    }

    init {
        // TODO: use this retrieved stuff to improve plan generation
        viewModelScope.launch {
            repository.getPlanMapPrograms().collect{
                updatePlans(workoutPlanMapPrograms = it.toList())
            }
        }
        viewModelScope.launch {
            preferences.getCurrentPlan().collect {
                updatePlans(currentPlanId = it)
            }
        }
    }

    fun onEvent(event: GeneratePlanEvent){
        when (event) {
            is GeneratePlanEvent.GeneratePlan -> {
                if (generatePlanJob == null) {
                    generatePlanJob = viewModelScope.launch {
                        val planId = generatePlan(
                            repository,
                            preferences,
                            event.goalChoice,
                            event.expertiseLevel,
                            event.workoutSplit
                        )
                        preferences.setCurrentPlan(planId, true)  // FIXME: I don't remember why I would need override

                        _state.update { it.copy(
                            generatedPlan = repository.getPlan(planId).first()
                        ) }
                    }
                }
            }
        }
    }

}
