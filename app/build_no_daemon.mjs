#!/usr/bin/env zx
import { spawn } from 'child_process';
import { promises as fs } from 'fs';

async function run() {
  const logFile = '/app/build_output.log';
  await fs.writeFile(logFile, "Starting build with --no-daemon...\n");

  const build = spawn('gradle', ['--no-daemon', 'assembleDebug']);

  build.stdout.on('data', async (data) => {
    await fs.appendFile(logFile, data.toString());
  });

  build.stderr.on('data', async (data) => {
    await fs.appendFile(logFile, data.toString());
  });

  build.on('close', async (code) => {
    await fs.appendFile(logFile, `\nBuild process completed with exit code: ${code}\n`);
  });
}

run().catch(console.error);
