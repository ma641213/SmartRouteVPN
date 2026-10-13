# SmartRoute VPN — Android UI prototype 0.2.1

## Read this before installing

**This is a UI/settings prototype, not a working VPN.** It does not create an encrypted tunnel, connect to nodes, download or parse subscription content, switch live tunnels, aggregate bandwidth, or implement DNS-leak protection / traffic filtering. The screen explicitly says that the VPN core is not enabled. Do not treat this build as privacy protection.

## What was checked before packaging

- Reviewed the Android Gradle Plugin / Gradle / JDK versions against the official AGP 8.7 compatibility table (Gradle 8.9 and JDK 17).
- Added explicit Android SDK setup and installation of Android API 35 plus Build Tools 35.0.0 in the cloud workflow.
- The workflow is configured to run `testDebugUnitTest`, `lintDebug`, and `assembleDebug`, then confirm that a non-empty APK exists and produce a SHA-256 checksum.
- The workflow itself has **not** yet run in your GitHub repository. Therefore, a successful cloud build is not yet verified; the first run's result is the real confirmation.
- This workspace has no Android SDK or Gradle executable, so I could not compile or install the APK locally. I will not claim that a build or device test has passed when it has not.

## One-time upload and build

1. Create one **private** repository in GitHub (or use a private repository you already own).
2. Upload the **contents** of this ZIP to the repository's default branch named `main` (upload the files and folders inside `SmartRouteVPN`, not the outer ZIP itself).
3. Open the repository's **Actions** tab. If prompted, enable GitHub Actions.
4. Run **Build and check SmartRoute VPN prototype** once, or let it start after the upload/push to `main`.
5. Wait for the run to finish. Only a green successful run means the configured checks and build passed. Download `SmartRouteVPN-prototype-debug-apk` from the run's **Artifacts** section; it contains the APK and checksum.

If the run fails, keep the repository as-is and share the failed run's error/log. You should not need to upload a new ZIP just to fix code; subsequent fixes can be committed to the same repository.

## Technical details

- Android application ID: `tw.smartroute.vpn`
- Minimum Android: 8.0 (API 26)
- Compile / target SDK: 35
- Android Gradle Plugin: 8.7.3; Gradle: 8.9; JDK: 17
- Features currently present: native Kotlin prototype UI, Traditional/Simplified Chinese selector, locally saved HTTPS subscription URL list with simple validation and deduplication, auto-switch preference toggle, and informational node-cleanup rules.
- Features not implemented: VPN service/core, tunnel protocol integration, subscription fetching/parsing, node health checks, automatic failover, multipath aggregation, DNS/IP leak prevention, domain filtering, anti-blocking, and security hardening.

## References

- Android Gradle Plugin 8.7 compatibility: https://developer.android.com/build/releases/agp-8-7-0-release-notes
- Android SDK setup action: https://github.com/android-actions/setup-android
- Android `VpnService` API: https://developer.android.com/reference/android/net/VpnService
- sing-box Android client documentation: https://sing-box.sagernet.org/zh/clients/android/
