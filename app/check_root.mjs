#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function listDir(dir) {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      const fullPath = join(dir, file);
      try {
        const stat = await fs.stat(fullPath);
        if (stat.isDirectory()) {
          console.log(`📁 ${fullPath}`);
        } else {
          console.log(`📄 ${fullPath} (${stat.size} bytes)`);
        }
      } catch (e) {}
    }
  } catch (err) {
    console.error(`Error listing ${dir}:`, err.message);
  }
}

console.log("Listing /root:");
await listDir('/root');

console.log("\nListing /var/tmp:");
await listDir('/var/tmp');
