# Instruções de Revert - LocalPulse

## Objetivo
Reverter o repositório para o commit `5e156f95` (feat: implement interactive lessons in ProTipsScreen)

## Commits a serem desfeitos
- ❌ `cdd7e819` - fix(ui): update state cast to HomeUiState.Content
- ❌ `8bb292a7` - build: update application ID and version code  
- ❌ `5e156f95` - feat: implement interactive lessons in ProTipsScreen
- ❌ `303b3d10` - refactor(qrcode): remove tutorial overlay from screen
- ❌ `c6e906ed` - feat: add ProTips screen navigation and UI
- ❌ (e todos os posteriores até `1bc3877d`)

## Opção 1: Reset Hard (Recomendado se quer limpar)
```bash
git reset --hard 5e156f95fa6b2ff07ceefcf38b8954bc732a26d8
git push origin main --force
```

## Opção 2: Revert (Mantém histórico - Mais seguro)
```bash
git revert --no-commit HEAD~7..HEAD
git commit -m "revert: restore to commit 5e156f95"
git push origin main
```

---
**Status**: Pronto para executar
**Data**: 2026-06-23
