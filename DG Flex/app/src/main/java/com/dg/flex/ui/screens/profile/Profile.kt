package com.dg.flex.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dg.flex.R
import com.dg.flex.data.db.entity.Sex
import com.dg.flex.data.db.entity.Theme
import com.dg.flex.navigation.BottomNavigationGraph
import com.dg.flex.navigation.FadeTransition
import com.dg.flex.ui.common.GroupedCard
import com.dg.flex.ui.common.InfoDialog
import com.dg.flex.utils.getLangPreferenceDropdownEntries
import com.dg.flex.utils.plus
import android.os.Build
import com.dg.flex.data.HealthConnectRepository
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.health.connect.client.PermissionController
import androidx.compose.foundation.background
import com.dg.flex.shared.maybeKgToLb
import com.dg.flex.shared.maybeLbToKg
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndSelectAll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.android.awaitFrame
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.pow
import kotlin.math.roundToInt

@Destination<BottomNavigationGraph>(style = FadeTransition::class)
@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
fun Profile(
    destinationsNavigator: DestinationsNavigator,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var editName by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var bmiDialogueShown by remember { mutableStateOf(false) }

    InfoDialog(dialogueIsOpen = bmiDialogueShown,
        toggleDialogue = { bmiDialogueShown = !bmiDialogueShown }
    ) {
        Text(stringResource(R.string.bmi_info))
    }
    val createDocForDbBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(mimeType = "application/x-sqlite3")
    ) { uri ->
        uri?.let { viewModel.onEvent(ProfileEvent.ExportDatabase(it)) }
    }

    val openDocForDbRestore = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.onEvent(ProfileEvent.ImportDatabase(it)) }
    }
    val createDocForPreferencesBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(mimeType = "application/json")
    ) { uri ->
        uri?.let { viewModel.onEvent(ProfileEvent.ExportPreferences(uri)) }
    }
    val openDocForPreferencesRestore = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.onEvent(ProfileEvent.ImportPreferences(it)) }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = state.backupOutcomeResId?.let { stringResource(it) }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
        viewModel.onEvent(ProfileEvent.ResetOutcomeMessage)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding.plus(PaddingValues(vertical = 16.dp)),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 16.dp),
        ) {
            // Header Section
            item {
                val nameTextFieldState = rememberTextFieldState()
                ProfileHeader(
                    name = state.name,
                    editName = editName,
                    nameTextFieldState = nameTextFieldState,
                    onEditToggle = { newName ->
                        editName = !editName
                        if (!editName) {
                            viewModel.onEvent(ProfileEvent.UpdateName(newName))
                        }
                    },
                    onNameSubmit = { newName ->
                        keyboardController?.hide()
                        editName = false
                        viewModel.onEvent(ProfileEvent.UpdateName(newName))
                    }
                )
            }

            // Personal Information Section
            item {
                ProfileSection(title = stringResource(R.string.personal_information_title)) {
                    PersonalInfoContent(
                        userSex = state.sex,
                        userBirthday = state.userBirthday,
                        updateBirthday = {
                            viewModel.onEvent(ProfileEvent.UpdateBirthday(it))
                        },
                        onEditSex = {
                            viewModel.onEvent(ProfileEvent.UpdateSex(it))
                        }
                    )
                }
            }

            // Physical Measurements Section
            item {
                ProfileSection(title = stringResource(R.string.physical_measurements_title)) {
                    PhysicalMeasurementsContent(
                        profileState = state,
                        viewModel = viewModel,
                        keyboardController = keyboardController,
                        focusManager = focusManager,
                        onBmiInfoClick = { bmiDialogueShown = true }
                    )
                }
            }


            item {
                ProfileSection(title = stringResource(R.string.acknowledgements_title)) {
                    Text(
                        stringResource(R.string.acknowledgements),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileHeader(
    name: String,
    editName: Boolean,
    nameTextFieldState: TextFieldState,
    onEditToggle: (String) -> Unit,
    onNameSubmit: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(editName) {
        if (editName) {
            nameTextFieldState.setTextAndSelectAll(name)
            awaitFrame()
            awaitFrame()
            awaitFrame()
            awaitFrame()
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!editName) {
                    Text(
                        text = if (name.isNotBlank())
                            stringResource(R.string.salute_user, name)
                        else
                            stringResource(R.string.what_is_your_name),
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onEditToggle(nameTextFieldState.text.toString()) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Edit, stringResource(R.string.edit_icon_user_name))
                    }
                } else {
                    OutlinedTextField(
                        state = nameTextFieldState,
                        isError = nameTextFieldState.text.isBlank(),
                        label = { Text(stringResource(R.string.name)) },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            capitalization = KeyboardCapitalization.Words
                        ),
                        onKeyboardAction = KeyboardActionHandler {
                            onNameSubmit(nameTextFieldState.text.toString())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                            cursorColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                    )
                    IconButton(
                        onClick = { onEditToggle(nameTextFieldState.text.toString()) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Done, stringResource(R.string.done_icon))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.header_to_content_padding))
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PersonalInfoContent(
    userSex: Sex,
    userBirthday: ZonedDateTime,
    updateBirthday: (ZonedDateTime) -> Unit,
    onEditSex: (Sex) -> Unit,
) {
    var editYear by remember { mutableStateOf(false) }
    if (editYear) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDate = LocalDate.of(userBirthday.year, userBirthday.month, userBirthday.dayOfMonth)
        )
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            onDismissRequest = {
                editYear = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        editYear = false
                        updateBirthday(Instant.ofEpochMilli(datePickerState.selectedDateMillis!!).atZone(
                            ZoneId.of("UTC")
                        ))
                    },
                    enabled = confirmEnabled.value,
                ) {
                    Text(stringResource(R.string.dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { editYear = false }) { Text(stringResource(R.string.dialog_cancel)) }
            },
        ) {
            // The verticalScroll will allow scrolling to show the entire month in case there is not
            // enough horizontal space (for example, when in landscape mode).
            // Note that it's still currently recommended to use a DisplayMode.Input at the state in
            // those cases.
            DatePicker(
                state = datePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Age Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val age = ChronoUnit.YEARS.between(userBirthday, ZonedDateTime.now())

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.age),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.i_years, age),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            IconButton(onClick = { editYear = true }) {
                if (editYear) {
                    Icon(Icons.Default.Close, stringResource(R.string.cancel_icon))
                } else {
                    Icon(Icons.Default.Edit, stringResource(R.string.edit_icon_age))
                }
            }
        }

        // Biological Sex Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.sex),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Row(
                Modifier
                    .padding(horizontal = 8.dp)
                    .weight(5f),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            ) {
                Sex.entries.forEachIndexed { index, sex ->
                    val modifier = if (sex == userSex)
                        Modifier.weight(1f + ButtonGroupDefaults.ExpandedRatio) // expanded
                    else Modifier.weight(1f)

                    ToggleButton(
                        checked = sex == userSex,
                        onCheckedChange = { onEditSex(sex) },
                        modifier = modifier,
                        shapes =
                            when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                Sex.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                    ) {
                        Text(stringResource(sex.displayRes))
                    }
                }
            }
        }
    }
}

@Composable
fun PhysicalMeasurementsContent(
    profileState: ProfileState,
    viewModel: ProfileViewModel,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    onBmiInfoClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Weight
        MeasurementRow(
            label = stringResource(R.string.weight),
            value = maybeKgToLb(profileState.weight, profileState.imperialSystem),
            unit = if (profileState.imperialSystem) stringResource(R.string.lb) else stringResource(R.string.kg),
            onValueChange = { newWeight ->
                viewModel.onEvent(ProfileEvent.UpdateWeight(
                    maybeLbToKg(newWeight, profileState.imperialSystem)
                ))
            },
            keyboardController = keyboardController,
            focusManager = focusManager
        )

        // Height
        if (profileState.imperialSystem) {
            var isEditing by remember { mutableStateOf(false) }
            var feetText by remember { mutableStateOf((profileState.height / 2.54f / 12).toInt().toString()) }
            var inchesText by remember { mutableStateOf(((profileState.height / 2.54f) % 12).toInt().toString()) }
            val feetIsValid = feetText.toFloatOrNull() != null && feetText.toFloat() > 0
            val inchesIsValid = inchesText.toFloatOrNull() != null && inchesText.toFloat() in 0f..11f

            LaunchedEffect(profileState.height) {
                if (!isEditing) {
                    feetText = (profileState.height / 2.54f / 12).toInt().toString()
                    inchesText = ((profileState.height / 2.54f) % 12).toInt().toString()
                }
            }

            val submitValue = {
                if (feetIsValid && inchesIsValid) {
                    viewModel.onEvent(
                        ProfileEvent.UpdateHeight(
                            (feetText.toFloat() * 12 + inchesText.toFloat()) * 2.54f
                        )
                    )
                    isEditing = false
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isEditing) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.height),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${feetText}ft ${inchesText}in",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, stringResource(R.string.edit_icon_measurement_i, stringResource(R.string.height)))
                    }
                } else {
                    OutlinedTextField(
                        value = feetText,
                        onValueChange = { feetText = it },
                        label = { Text(stringResource(R.string.height)) },
                        suffix = { Text("ft") },
                        isError = !feetIsValid,
                        supportingText = {
                            if (!feetIsValid) {
                                Text(stringResource(R.string.please_enter_a_valid_number))
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { submitValue() }),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = inchesText,
                        onValueChange = { inchesText = it },
                        label = { Text(stringResource(R.string.height)) },
                        suffix = { Text("in") },
                        isError = !inchesIsValid,
                        supportingText = {
                            if (!inchesIsValid) {
                                Text(stringResource(R.string.please_enter_a_valid_number))
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { submitValue() }),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (inchesIsValid && feetIsValid) {
                                submitValue()
                            } else {
                                feetText = (profileState.height / 2.54f / 12).toInt().toString()
                                inchesText = ((profileState.height / 2.54f) % 12).toInt().toString()
                                isEditing = false
                            }
                        }
                    ) {
                        if (inchesIsValid && feetIsValid) {
                            Icon(Icons.Default.Done, stringResource(R.string.done_icon))
                        } else {
                            Icon(Icons.Default.Close, stringResource(R.string.cancel_icon))
                        }
                    }
                }
            }
        } else {
            MeasurementRow(
                label = stringResource(R.string.height),
                value = profileState.height,
                unit = "cm",
                onValueChange = { newHeight ->
                    viewModel.onEvent(
                        ProfileEvent.UpdateHeight(
                            newHeight
                        )
                    )
                },
                keyboardController = keyboardController,
                focusManager = focusManager
            )
        }

        // BMI
        val bmi = if (profileState.height != 0f)
            profileState.weight / (profileState.height/100).pow(2)
        else
            0f
        val bmiCategory = when {
            bmi < 18.5f -> stringResource(R.string.underweight)
            bmi > 30f -> stringResource(R.string.obese)
            bmi > 25f -> stringResource(R.string.overweight)
            else -> stringResource(R.string.normal)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.body_mass_index),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(bmi * 10).roundToInt() / 10.0} ($bmiCategory)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            IconButton(onClick = onBmiInfoClick) {
                Icon(Icons.AutoMirrored.Filled.HelpOutline, stringResource(R.string.info_icon_bmi))
            }
        }
    }
}

@Composable
fun MeasurementRow(
    label: String,
    value: Float,
    unit: String,
    onValueChange: (Float) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf(value.toString()) }
    val isValid by remember { derivedStateOf {
        textValue.toFloatOrNull() != null && textValue.toFloat() > 0
    }}

    LaunchedEffect(value) {
        if (!isEditing) {
            textValue = value.toString()
        }
    }

    val submitValue = {
        if (isValid) {
            onValueChange(textValue.toFloat())
            isEditing = false
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isEditing) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${value.toInt()} $unit",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            IconButton(onClick = { isEditing = true }) {
                Icon(Icons.Default.Edit, stringResource(R.string.edit_icon_measurement_i, label))
            }
        } else {
            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = { Text(label) },
                suffix = { Text(unit) },
                isError = !isValid,
                supportingText = {
                    if (!isValid) {
                        Text(stringResource(R.string.please_enter_a_valid_number))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { submitValue() }),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    if (isValid) {
                        submitValue()
                    } else {
                        textValue = value.toString()
                        isEditing = false
                    }
                }
            ) {
                if (isValid) {
                    Icon(Icons.Default.Done, stringResource(R.string.done_icon))
                } else {
                    Icon(Icons.Default.Close, stringResource(R.string.cancel_icon))
                }
            }
        }
    }
}
