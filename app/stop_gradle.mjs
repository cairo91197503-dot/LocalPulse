#!/usr/bin/env zx
import { exec } from 'child_process';
import { promisify } from 'util';

const execAsync = promisify(exec);

try {
  console.log("Stopping Gradle daemons...");
  try {
    await execAsync('gradle --stop');
  } catch (e) {}

  console.log("Killing any lingering Java, Gradle or competing node processes...");
  for (const proc of ['java', 'gradle', 'run_gradle']) {
    try {
      await execAsync(`pkill -9 -f ${proc}`);
      console.log(`Successfully killed ${proc} processes.`);
    } catch (e) {}
  }
} catch (err) {
  console.error("Failed to run gradle stop:", err.message);
}
