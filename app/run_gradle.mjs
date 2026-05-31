#!/usr/bin/env zx
import { exec } from 'child_process';
import { promisify } from 'util';

const execAsync = promisify(exec);

try {
  console.log("Running gradle assembleDebug...");
  const { stdout, stderr } = await execAsync('gradle assembleDebug');
  console.log("STDOUT:\n", stdout);
  if (stderr) {
    console.log("STDERR:\n", stderr);
  }
} catch (err) {
  console.error("GRADLE BUILD FAILED:\n", err.stdout || err.message);
  if (err.stderr) {
    console.error("STDERR:\n", err.stderr);
  }
}
