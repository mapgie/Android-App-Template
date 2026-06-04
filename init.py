#!/usr/bin/env python3
"""
init.py — One-time setup: replace template placeholders with your app's identity.

Usage:
    python3 init.py <AppName> <com.your.package>

Example:
    python3 init.py TaskTracker com.acme.tasktracker

Replacements applied:
    com.example.myapp   → com.your.package
    MyAppTypography     → AppNameTypography
    MyAppTheme          → AppNameTheme
    MyApplication       → AppNameApplication
    Theme.MyApp         → Theme.AppName
    MyApp               → AppName
    myapp_database      → appname_database
    myapp_prefs         → appname_prefs

File/directory renames:
    app/src/main/java/com/example/myapp/  → app/src/main/java/your/package/path/
    MyApplication.kt                       → AppNameApplication.kt

After running, delete this script and commit the result.
"""

import os
import re
import shutil
import sys
from pathlib import Path

# File types to perform text replacements in
TEXT_EXTENSIONS = {
    ".kt", ".xml", ".kts", ".toml", ".pro", ".properties",
    ".md", ".py", ".yml", ".yaml",
}

# Directories to skip entirely
SKIP_DIRS = {".git", "build", ".gradle", ".idea", "__pycache__"}


def replace_in_file(path: Path, replacements: list[tuple[str, str]]) -> bool:
    try:
        text = path.read_text(encoding="utf-8")
    except (OSError, UnicodeDecodeError):
        return False

    updated = text
    for old, new in replacements:
        updated = updated.replace(old, new)

    if updated != text:
        path.write_text(updated, encoding="utf-8")
        return True
    return False


def validate_app_name(name: str) -> str | None:
    if not re.match(r"^[A-Z][A-Za-z0-9]+$", name):
        return "must be CamelCase starting with a capital letter, e.g. TaskTracker"
    return None


def validate_package(pkg: str) -> str | None:
    if not re.match(r"^[a-z][a-z0-9]*(\.[a-z][a-z0-9]*){1,}$", pkg):
        return "must be dot-separated lowercase segments, e.g. com.acme.tasktracker"
    return None


def main() -> int:
    if len(sys.argv) != 3:
        print(__doc__)
        return 1

    app_name = sys.argv[1]
    package  = sys.argv[2]

    err = validate_app_name(app_name)
    if err:
        print(f"Error: AppName {err} (got '{app_name}')")
        return 1

    err = validate_package(package)
    if err:
        print(f"Error: package {err} (got '{package}')")
        return 1

    app_lower = app_name.lower()    # TaskTracker → tasktracker

    # Order matters: replace longer / more-specific strings before shorter ones
    # so that e.g. "MyApplication" is handled before the plain "MyApp" pass.
    replacements: list[tuple[str, str]] = [
        ("com.example.myapp",   package),
        ("MyAppTypography",     f"{app_name}Typography"),
        ("MyAppTheme",          f"{app_name}Theme"),
        ("MyApplication",       f"{app_name}Application"),
        ("Theme.MyApp",         f"Theme.{app_name}"),
        ("MyApp",               app_name),
        ("myapp_database",      f"{app_lower}_database"),
        ("myapp_prefs",         f"{app_lower}_prefs"),
    ]

    root = Path(__file__).resolve().parent
    changed: list[Path] = []

    for path in sorted(root.rglob("*")):
        if not path.is_file():
            continue
        # Skip special directories and this script itself
        if any(part in SKIP_DIRS for part in path.relative_to(root).parts):
            continue
        if path == Path(__file__).resolve():
            continue
        if path.suffix not in TEXT_EXTENSIONS:
            continue

        if replace_in_file(path, replacements):
            changed.append(path.relative_to(root))

    # ── Rename source directory ──────────────────────────────────────────────
    old_java_root = root / "app/src/main/java"
    old_src = old_java_root / "com/example/myapp"
    new_src = old_java_root / Path(*package.split("."))

    if old_src.exists() and old_src.resolve() != new_src.resolve():
        new_src.parent.mkdir(parents=True, exist_ok=True)
        shutil.move(str(old_src), str(new_src))
        print(f"Renamed dir : com/example/myapp/ → {package.replace('.', '/')}/")

        # Remove now-empty parent directories up to java/
        for parent in old_src.parents:
            if parent == old_java_root:
                break
            try:
                parent.rmdir()          # only succeeds if empty
            except OSError:
                break

    # ── Rename MyApplication.kt ──────────────────────────────────────────────
    old_kt = new_src / "MyApplication.kt"
    new_kt = new_src / f"{app_name}Application.kt"
    if old_kt.exists():
        old_kt.rename(new_kt)
        print(f"Renamed file: MyApplication.kt → {app_name}Application.kt")

    # ── Summary ──────────────────────────────────────────────────────────────
    print(f"\nUpdated {len(changed)} file(s):")
    for f in changed:
        print(f"  {f}")

    print(f"""
Done.
  App name : {app_name}
  Package  : {package}

Next steps:
  1. ./gradlew assembleDebug          — verify the build compiles
  2. python3 a11y_check.py            — verify no accessibility violations
  3. Delete init.py and commit        — it's a one-time tool
""")
    return 0


if __name__ == "__main__":
    sys.exit(main())
