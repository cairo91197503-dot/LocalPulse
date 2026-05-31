#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';
import { exec } from 'child_process';
import { promisify } from 'util';

const execAsync = promisify(exec);

// Check processes
try {
  const { stdout } = await execAsync('ps -ef');
  console.log("RUNNING PROCESSES:\n", stdout);
} catch (e) {}

// Check APK
async function checkFileExists(file) {
  try {
    const stats = await fs.stat(file);
    console.log(`FILE ${file} EXISTS (${stats.size} bytes).`);
  } catch (err) {
    console.log(`FILE ${file} DOES NOT EXIST.`);
  }
}

await checkFileExists('app/build/outputs/apk/debug/app-debug.apk');
