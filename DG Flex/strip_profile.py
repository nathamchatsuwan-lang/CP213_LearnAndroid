import sys
import gc

file_path = r'c:\Users\ASUS\Documents\GitHub\PerfectGymCoach-main\app\src\main\java\com\dg\flex\ui\screens\profile\Profile.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

out = []
skip = False
for i, line in enumerate(lines):
    if '// Preferences Section' in line:
        skip = True
    if '            item {' in line and i < len(lines) - 2 and 'ProfileSection(title = stringResource(R.string.acknowledgements_title))' in lines[i+1]:
        skip = False
    
    # Wait, the Acknowledgements section line is:
    #             item {
    #                 ProfileSection(title = stringResource(R.string.acknowledgements_title)) {
    
    if not skip:
        out.append(line)

with open(file_path, 'w', encoding='utf-8') as f:
    f.writelines(out)

print(f'Done stripping. Line count: {len(out)}')
