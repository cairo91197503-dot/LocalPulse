const { execSync } = require('child_process');
const fs = require('fs');
try {
  execSync('keytool -genkeypair -alias androiddebugkey -keypass android -keystore debug.keystore -storepass android -dname "CN=Android Debug,O=Android,C=US" -keyalg RSA -keysize 2048 -validity 10000 -v', { stdio: 'inherit' });
  const b64 = fs.readFileSync('debug.keystore').toString('base64');
  console.log('BASE64_KEYSTORE_START');
  console.log(b64);
  console.log('BASE64_KEYSTORE_END');
} catch (e) {
  console.error(e);
}
