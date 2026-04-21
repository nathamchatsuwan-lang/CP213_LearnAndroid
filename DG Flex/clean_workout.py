import re

file_path = r"c:\Users\ASUS\Documents\GitHub\PerfectGymCoach-main\app\src\main\java\com\dg\flex\ui\screens\workout\WorkoutViewModel.kt"
with open(file_path, "r", encoding="utf-8") as f:
    text = f.read()

# 1. Imports
text = re.sub(r'import com\.dg\.flex\.shared\.grpc.*?WorkoutWearServiceGrpcKt\n', '', text)
text = re.sub(r'import com\.dg\.flex\.shared\.urgentProtoDataStore\n', '', text)
text = re.sub(r'import com\.google\.android\.horologist\.annotations\.ExperimentalHorologistApi\n', '', text)
text = re.sub(r'import com\.google\.android\.horologist\.data\.WearDataLayerRegistry\n', '', text)
text = re.sub(r'import com\.dg\.flex\.data\.PhoneWorkoutRepository\n', '', text)
text = re.sub(r'import com\.dg\.flex\.shared\.grpc\.Workout\n', '', text)
text = re.sub(r'import com\.google\.protobuf\.Empty\n', '', text)
text = re.sub(r'import com\.dg\.flex\.shared\.WORKOUT_IMAGES_PATH\n', '', text)
text = re.sub(r'import com\.dg\.flex\.shared\.bitmapArrayStore\n', '', text)
text = re.sub(r'import com\.dg\.flex\.shared\.toProtoTimestamp\n', '', text)

# 2. Class signature
text = text.replace("@OptIn(InternalProperty::class, OutOfSyncProperty::class, ExperimentalHorologistApi::class)", "@OptIn(InternalProperty::class, OutOfSyncProperty::class)")
text = re.sub(r',\s*private val registry:\s*WearDataLayerRegistry,\s*private val phoneWorkoutRepository:\s*PhoneWorkoutRepository,\s*private val phoneToWatchService:\s*WorkoutWearServiceGrpcKt\.WorkoutWearServiceCoroutineStub\s*', '\n', text)

# 3. Variables
text = re.sub(r'\s*private val wearWorkoutStatic.*?\(viewModelScope\)', '', text)
text = re.sub(r'\s*private val wearWorkoutDynamic.*?\(viewModelScope\)', '', text)
text = re.sub(r'\s*private val wearWorkoutImages.*?WORKOUT_IMAGES_PATH\)', '', text)

# 4. Method calls in init & onCleared
text = re.sub(r'\s*phoneWorkoutRepository\.stopOngoingWorkout\(\)', '', text)
text = re.sub(r'\s*phoneWorkoutRepository\.startOngoingWorkout\(\)', '', text)
text = re.sub(r'\s*checkWorkoutDataChangesForWear\(\)', '', text)
text = re.sub(r'\s*observeSetCompletionsFromWear\(\)', '', text)
text = re.sub(r'\s*observeWorkoutCompletionsFromWear\(\)', '', text)
text = re.sub(r'\s*observeAcceptedModificationsFromWear\(\)', '', text)

# 5. Calls to watch
text = re.sub(r'\s*try\s*\{\s*phoneToWatchService\.scrollToExercise\([\s\S]*?\}\s*catch\s*\(e:\s*Exception\)\s*\{\s*Log\.e\("WorkoutViewModel",\s*"Failed to scroll to exercise on watch",\s*e\)\s*\}', '', text)
text = re.sub(r'\s*try\s*\{\s*phoneToWatchService\.setRest\([\s\S]*?\}\s*catch\s*\(e:\s*Exception\)\s*\{\s*Log\.e\("WorkoutViewModel",\s*"Failed to set rest on watch",\s*e\)\s*\}', '', text)

# special case for getHealthData
text = re.sub(r'val healthData = try\s*\{\s*phoneToWatchService\.getHealthData.*?\s*catch.*?\s*\}\s*', 'val healthData = null\n', text, flags=re.DOTALL)

# remove toProto()
text = re.sub(r'\s*fun toProto\(\).*?build\(\)', '', text, flags=re.DOTALL)

# 6. Wear functions deletion
text = re.sub(r'\s*private fun checkWorkoutDataChangesForWear\(\)\s*\{.*?(?=\s*private fun observeSetCompletionsFromWear\(\))', '', text, flags=re.DOTALL)
text = re.sub(r'\s*private fun observeSetCompletionsFromWear\(\)\s*\{.*?(?=\s*private fun observeWorkoutCompletionsFromWear\(\))', '', text, flags=re.DOTALL)
text = re.sub(r'\s*private fun observeWorkoutCompletionsFromWear\(\)\s*\{.*?(?=\s*private fun observeAcceptedModificationsFromWear\(\))', '', text, flags=re.DOTALL)
text = re.sub(r'\s*private fun observeAcceptedModificationsFromWear\(\)\s*\{.*?(?=\s*\})', '', text, flags=re.DOTALL)

# Other wear open invocations
text = re.sub(r'\s*repository\.openWearWorkout\(\)', '', text)
text = re.sub(r',\s*healthRecordId = healthData\?\.healthRecordId.*?,', ',', text, flags=re.DOTALL)
text = re.sub(r'healthRecordId.*?,', '', text)
text = re.sub(r'maxHeartRate.*?,', '', text)
text = re.sub(r'avgHeartRate.*?,', '', text)
text = re.sub(r'minHeartRate.*?,', '', text)
text = re.sub(r'heartRates =.*?,', '', text)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(text)
