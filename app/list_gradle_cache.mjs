#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function listDir(dir, depth = 0) {
  if (depth > 5) return;
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      if (['caches', 'wrapper', 'daemon', 'jdks'].includes(file)) continue;
      const fullPath = join(dir, file);
      const stat = await fs.stat(fullPath);
      if (stat.isDirectory()) {
         console.log(`${' '.repeat(depth)}📁 ${fullPath}`);
         await listDir(fullPath, depth + 1);
      } else {
         console.log(`${' '.repeat(depth)}📄 ${fullPath} (${stat.size} bytes)`);
      }
    }
  } catch (err) {}
}

console.log("Listing /root/.gradle recursively (excluding caches):");
await listDir('/root/.gradle');

console.log("\nListing /opt/gradle/.gradle recursively (excluding caches):");
await listDir('/opt/gradle/.gradle');
