#!/usr/bin/env zx

import { promises as fs } from 'fs';
import { join } from 'path';

async function findFile(dir, fileName) {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      if (file === 'node_modules' || file === '.gradle' || file === 'build' || file === 'proc' || file === 'sys' || file === 'dev') continue;
      const fullPath = join(dir, file);
      try {
        const stat = await fs.stat(fullPath);
        if (stat.isDirectory()) {
          await findFile(fullPath, fileName);
        } else if (file === fileName) {
          console.log(`FOUND FILE AT: ${fullPath} (${stat.size} bytes)`);
        }
      } catch (e) {}
    }
  } catch (err) {}
}

console.log("Searching for MainApp.kt in the entire filesystem...");
await findFile('/', 'MainApp.kt');
console.log("Search complete.");
