# Bite and Melt - Instagram Helper (Android)

Aa nani app Instagram ni "started following you" notification POS ne moklse. POS ma New Bill ma "Recently followed" ma naam dekhase.

## APK banavvani rit (GitHub, free)
1. github.com ma login karo > upar **+ > New repository**. Name: `bite-and-melt-helper`. **Public** rakho. **Create repository**.
2. **uploading an existing file** link dabavo. Aa folder ni **badhi files ane folders** (app, .github, build.gradle, settings.gradle, gradle.properties) kheechi ne mukho. **Commit changes** dabavo.
   - `.github` folder upload na thay to: **Add file > Create new file**, name ma `.github/workflows/build-apk.yml` lakho, ane `.github/workflows/build-apk.yml` ni andar nu lakhan paste karo > Commit.
3. **Actions** tab kholo. "Build APK" chalu thase (ke **Run workflow** dabavo). 5-10 minute ma lilo tick aavse.
4. Te run kholo > niche **Artifacts** ma `BiteMelt-Insta-Helper-APK` download karo > zip extract karo > `app-debug.apk`.
5. APK ne Android phone ma moklo (WhatsApp/Drive) > kholo > install (Install unknown apps allow karo; "Play Protect" kahe to "Install anyway").

## Phone ma setup (ek vaar)
1. App kholo > **1. Notification access chalu karo** > "Bite and Melt Helper" ON.
2. **2. Battery restriction band karo** > aa app ne "Unrestricted / Don't optimize".
3. POS ma Settings > **Instagram follower helper** ma **POS link** ane **Helper key** che. App ma lakho > **Save ane test karo** > "Connected OK" aave.
4. Instagram app ni notification settings ma follow ni notification ON hovi joiye.

Aa app fakt Instagram ni notification jove che jema "follow" lakhelu hoy. Biju kai vanchtu nathi.
