#!/usr/bin/env zx
import { exec } from 'child_process';
import { promisify } from 'util';

const execAsync = promisify(exec);

try {
  console.log("Killing PIDs 623, 635, 636...");
  for (const pid of [623, 635, 636]) {
    try {
      await execAsync(`kill -9 ${pid}`);
      console.log(`Killed PID ${pid}`);
    } catch (e) {
      console.log(`Failed to kill PID ${pid}:`, e.message);
    }
  }
} catch (err) {
  console.error("Failed to run kill:", err.message);
}
