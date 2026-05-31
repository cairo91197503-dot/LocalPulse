#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function findFiles(dir) {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      if (['node_modules', '.gradle', 'build', 'proc', 'sys', 'dev', 'usr', 'lib', 'lib64', 'etc', 'var', 'sbin', 'bin', 'boot', 'run'].includes(file)) continue;
      const fullPath = join(dir, file);
      try {
        const stat = await fs.stat(fullPath);
        if (stat.isDirectory()) {
          await findFiles(fullPath);
        } else if (file.endsWith('.gradle.kts') || file.endsWith('.gradle') || file.endsWith('.kt')) {
          console.log(`FOUND: ${fullPath} (${stat.size} bytes)`);
        }
      } catch (e) {}
    }
  } catch (err) {}
}

console.log("Searching for gradle/kotlin files outside system dirs...");
await findFiles('/');
console.log("Search complete.");
