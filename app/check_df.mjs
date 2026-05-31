#!/usr/bin/env zx
import { exec } from 'child_process';
import { promisify } from 'util';

const execAsync = promisify(exec);

try {
  const { stdout } = await execAsync('df -h');
  console.log("FILESYSTEMS:\n", stdout);
} catch (err) {
  console.error("Failed to run df:", err.message);
}
