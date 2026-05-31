#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function findFile(dir, target) {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      if (['node_modules', '.gradle', 'build', 'proc', 'sys', 'dev'].includes(file)) continue;
      const fullPath = join(dir, file);
      try {
        const stat = await fs.stat(fullPath);
        if (stat.isDirectory()) {
          await findFile(fullPath, target);
        } else if (file === target) {
          console.log(`FOUND ${target}: ${fullPath}`);
        }
      } catch (e) {}
    }
  } catch (err) {}
}

console.log("Searching for MainActivity.kt...");
await findFile('/', 'MainActivity.kt');
console.log("Search complete.");
