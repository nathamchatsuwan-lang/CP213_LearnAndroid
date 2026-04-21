package com.dg.flex.ui.screens.programs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dg.flex.R
import com.dg.flex.data.db.entity.getDaysOfWeekNames
import com.dg.flex.data.db.entity.getPlanDisplayName
import com.dg.flex.data.db.entity.getProgramDisplayName
import com.dg.flex.navigation.ChangePlanGraph
import com.dg.flex.navigation.SlideTransition
import com.dg.flex.ui.common.EmptyScreenInfo
import com.dg.flex.ui.common.InsertNameDialog
import com.dg.flex.ui.common.WorkoutCard
import com.dg.flex.ui.screens.programs.components.ProgramCard
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.AddProgramExerciseDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.android.awaitFrame
import sh.calvin.reorderable.rememberReorderableLazyListState
import com.dg.flex.data.db.entity.WorkoutProgram
import com.dg.flex.data.db.entity.WorkoutProgramRename
import com.dg.flex.data.db.entity.WorkoutProgramReorder

@Destination<ChangePlanGraph>(style = SlideTransition::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun AddProgram(
    navigator: DestinationsNavigator,
    planId: Long,
    openDialogNow: Boolean = false,
    viewModel: ProgramsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    LaunchedEffect(planId) {
        viewModel.onEvent(ProgramsEvent.InitProgramView(planId))
    }

    InsertNameDialog(
        prompt = stringResource(R.string.new_program_prompt),
        dialogueIsOpen = state.openAddProgramDialog,
        toggleDialog = { viewModel.onEvent(ProgramsEvent.ToggleAddProgramDialog) },
        insertName = { programName ->
            viewModel.onEvent(ProgramsEvent.AddProgram(WorkoutProgram(
                extPlanId = planId,
                name = programName,
                orderInWorkoutPlan = state.programs.size
            ))) }
    )
    // rename program
    InsertNameDialog(
        prompt = stringResource(R.string.rename_program_prompt),
        dialogueIsOpen = state.openChangeNameDialog,
        toggleDialog = { viewModel.onEvent(ProgramsEvent.ToggleChangeNameDialog()) },
        oldName = state.programs.firstOrNull {
            it.programId == state.programToBeChanged
        }?.name?.let { getProgramDisplayName(it)},
        insertName = { viewModel.onEvent(ProgramsEvent.RenameProgram(
            WorkoutProgramRename(
                programId = state.programToBeChanged,
                name = it
            )
        )) }
    )
    SelectDaysDialog(
        isOpen = state.openDaysSelectionDialog,
        onDismiss = { viewModel.onEvent(ProgramsEvent.ToggleDaysSelectionDialog()) },
        selectedDays = state.programs.find { it.programId == state.programForDaysSelection }?.daysOfWeek ?: emptyList(),
        onDaysSelected = { days ->
            viewModel.onEvent(ProgramsEvent.UpdateProgramDays(state.programForDaysSelection, days))
        }
    )
    val openDialog = rememberSaveable { mutableStateOf(openDialogNow) }
    LaunchedEffect(openDialog.value) {
        if (openDialog.value){
            awaitFrame()
            awaitFrame()
            viewModel.onEvent(ProgramsEvent.ToggleAddProgramDialog)
            openDialog.value = false
        }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = { Text(
                    getPlanDisplayName(state.planName)
                ) },
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.navigateUp() },
                        shapes = IconButtonDefaults.shapes(),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back_icon)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }, floatingActionButton = {
            MediumFloatingActionButton (
                onClick = {
                    viewModel.onEvent(ProgramsEvent.ToggleAddProgramDialog)
                },
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_program),
                    modifier = Modifier.size(FloatingActionButtonDefaults.MediumIconSize)
                )
            }
        }, content = { innerPadding ->
            if (state.programs.isEmpty()) {
                // if you have no programs
                EmptyScreenInfo(
                    Icons.Default.Description,
                    R.string.empty_no_programs,
                    titleRes = R.string.empty_no_programs,
                    subtitleRes = R.string.empty_home_program
                )
            } else {
                // if you have some programs
                val isDragging = remember { mutableStateOf(false) }
                val listState = rememberLazyListState()
                val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
                    val toIndex = state.programs.find { it.programId == to.key }!!.orderInWorkoutPlan
                    val fromIndex = state.programs.find { it.programId == from.key }!!.orderInWorkoutPlan
                    while (viewModel.reorderCompleted.tryReceive().isSuccess);

                    viewModel.onEvent(ProgramsEvent.ReorderProgram(listOf(
                        WorkoutProgramReorder(
                            from.key as Long,
                            toIndex
                        ),
                        WorkoutProgramReorder(
                            to.key as Long,
                            fromIndex
                        )
                    )))
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                    while (viewModel.reorderCompleted.receive()) {
                        // check reorder completed
                        if (state.programs.find { it.programId == from.key }!!.orderInWorkoutPlan == toIndex &&
                            state.programs.find { it.programId == to.key }!!.orderInWorkoutPlan == fromIndex
                        )
                            break
                    }
                }
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = innerPadding
                ) {
                    itemsIndexed(items = state.programs, key = { _, it -> it.programId }) { index, programEntry ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .animateItem(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(8.dp).widthIn(min = 64.dp)
                            ) {
                                Text(
                                    stringResource(R.string.day),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                IconButton(onClick = {
                                    viewModel.onEvent(ProgramsEvent.ToggleDaysSelectionDialog(programEntry.programId))
                                }) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        stringResource(R.string.select_days),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                val dayNames = getDaysOfWeekNames(programEntry.daysOfWeek, context)
                                Text(
                                    text = dayNames.ifEmpty { stringResource(R.string.not_scheduled) },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (dayNames.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.widthIn(max = 64.dp)
                                )
                            }
                            ProgramCard(
                                navigator = navigator,
                                reorderableState = reorderableState,
                                isDragging = isDragging,
                                program = programEntry,
                                exercises = state.exercisesAndInfo[programEntry.programId]
                                    ?: emptyList(),
                                onCardClick = {
                                    navigator.navigate(
                                        AddProgramExerciseDestination(
                                            programName = programEntry.name,
                                            programId = programEntry.programId
                                        )
                                    )
                                }, onRename = {
                                    viewModel.onEvent(
                                        ProgramsEvent.ToggleChangeNameDialog(
                                            programEntry.programId
                                        )
                                    )
                                }, onDelete = {
                                    viewModel.onEvent(ProgramsEvent.DeleteProgram(programEntry.programId))
                                },
                                onDuplicate = {
                                    viewModel.onEvent(ProgramsEvent.DuplicateProgram(programEntry.programId))
                                },
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    }
                    item{
                        var finalSpacerSize = 80.dp + 16.dp // large fab size + its padding FIXME: not hardcode
                        finalSpacerSize += 8.dp
                        Spacer(Modifier.height(finalSpacerSize))
                    }
                }
            }
        })
}

@Composable
fun SelectDaysDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    selectedDays: List<Int>,
    onDaysSelected: (List<Int>) -> Unit
) {
    if (!isOpen) return
    var currentSelection by remember(selectedDays) { mutableStateOf(selectedDays) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_days)) },
        text = {
            Column {
                val days = listOf(
                    1 to stringResource(R.string.monday),
                    2 to stringResource(R.string.tuesday),
                    3 to stringResource(R.string.wednesday),
                    4 to stringResource(R.string.thursday),
                    5 to stringResource(R.string.friday),
                    6 to stringResource(R.string.saturday),
                    7 to stringResource(R.string.sunday)
                )
                days.forEach { (id, name) ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()) {
                        Checkbox(
                            checked = currentSelection.contains(id),
                            onCheckedChange = { checked ->
                                currentSelection = if (checked) {
                                    (currentSelection + id).distinct()
                                } else {
                                    currentSelection - id
                                }
                            }
                        )
                        Text(name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDaysSelected(currentSelection); onDismiss() }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

