#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function findArchives(dir) {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      if (['node_modules', '.gradle', 'build', 'proc', 'sys', 'dev', 'usr', 'lib', 'lib64', 'etc', 'var', 'sbin', 'bin', 'boot', 'run'].includes(file)) continue;
      const fullPath = join(dir, file);
      try {
        const stat = await fs.stat(fullPath);
        if (stat.isDirectory()) {
          await findArchives(fullPath);
        } else if (file.endsWith('.zip') || file.endsWith('.tar.gz') || file.endsWith('.tgz') || file.endsWith('.tar')) {
          console.log(`FOUND ARCHIVE: ${fullPath} (${stat.size} bytes)`);
        }
      } catch (e) {}
    }
  } catch (err) {}
}

console.log("Searching for archives...");
await findArchives('/');
console.log("Search complete.");
