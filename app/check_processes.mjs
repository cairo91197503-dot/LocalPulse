#!/usr/bin/env zx
import { exec } from 'child_process';
import { promisify } from 'util';

const execAsync = promisify(exec);

try {
  const { stdout } = await execAsync('ps -ef');
  console.log("RUNNING PROCESSES:\n", stdout);
} catch (err) {
  console.error("Failed to run ps:", err.message);
}
