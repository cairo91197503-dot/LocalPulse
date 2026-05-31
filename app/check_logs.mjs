#!/usr/bin/env zx
import { promises as fs } from 'fs';
import { join } from 'path';

async function listLogs(dir) {
  try {
    const files = await fs.readdir(dir);
    for (const file of files) {
      const fullPath = join(dir, file);
      try {
        const stat = await fs.stat(fullPath);
        if (stat.isDirectory()) {
          console.log(`📁 LOG DIR: ${fullPath}`);
          await listLogs(fullPath);
        } else {
          console.log(`📄 LOG FILE: ${fullPath} (${stat.size} bytes)`);
          if (file.endsWith('.log') || file.endsWith('.txt') || file.endsWith('.json')) {
            let content = await fs.readFile(fullPath, 'utf8');
            console.log(`--- Content of ${file} ---`);
            console.log(content.substring(0, 1000));
            console.log(`-------------------------`);
          }
        }
      } catch (e) {}
    }
  } catch (err) {}
}

console.log("Checking /var/log:");
await listLogs('/var/log');
